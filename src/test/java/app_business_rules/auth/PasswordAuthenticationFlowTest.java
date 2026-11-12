package app_business_rules.auth;

import app_business_rules.login.*;
import app_business_rules.logout.*;
import app_business_rules.refresh.*;
import app_business_rules.signup.*;
import entities.users.User;
import frameworks_and_drivers.security.Pbkdf2PasswordHasher;
import interface_adapters.session.InMemorySessionState;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests sign-up, login, and logout with real password hashing.
 */
class PasswordAuthenticationFlowTest {

    @Test
    void testSignUpLoginLogout() {
        TestContext context = new TestContext();

        context.signUp.execute(new SignUpInputData(
                "testuser", "password123", "Test", "User"));

        User saved = context.users.get("testuser");

        assertNotNull(saved);
        assertNotEquals("password123", saved.getPasswordHash());
        assertTrue(context.hasher.verify(
                "password123", saved.getPasswordHash()));

        context.login.execute(
                new LoginInputData("testuser", "password123"));

        assertEquals(saved.getUserId(),
                context.session.getCurrentUserId());

        context.logout.execute(
                new LogoutInputData(saved.getUserId()));

        assertNull(context.session.getCurrentUserId());
    }

    @Test
    void testIncorrectPasswordDoesNotLogIn() {
        TestContext context = new TestContext();

        context.signUp.execute(new SignUpInputData(
                "testuser", "password123", "Test", "User"));

        context.login.execute(
                new LoginInputData("testuser", "incorrect"));

        assertNull(context.session.getCurrentUserId());
        assertEquals("Invalid username or password.",
                context.loginPresenter.error);
    }

    @Test
    void testDifferentUsersHaveDifferentHashes() {
        TestContext context = new TestContext();

        context.signUp.execute(new SignUpInputData(
                "alice", "samepassword", "Alice", "One"));

        context.signUp.execute(new SignUpInputData(
                "bob", "samepassword", "Bob", "Two"));

        User alice = context.users.get("alice");
        User bob = context.users.get("bob");

        assertNotEquals(alice.getPasswordHash(), bob.getPasswordHash());
        assertTrue(context.hasher.verify(
                "samepassword", alice.getPasswordHash()));
        assertTrue(context.hasher.verify(
                "samepassword", bob.getPasswordHash()));
    }

    private static class TestContext {
        private final Map<String, User> users = new HashMap<>();

        private final Pbkdf2PasswordHasher hasher =
                new Pbkdf2PasswordHasher();

        private final InMemorySessionState session =
                new InMemorySessionState();

        private final LoginPresenterSpy loginPresenter =
                new LoginPresenterSpy();

        private final SignUpOutputBoundary signUpPresenter =
                new SignUpOutputBoundary() {
                    @Override
                    public void prepareSuccessView(
                            SignUpOutputData outputData) {
                    }

                    @Override
                    public void prepareFailView(String message) {
                        fail("Unexpected sign-up failure: " + message);
                    }
                };

        private final LogoutOutputBoundary logoutPresenter =
                new LogoutOutputBoundary() {
                    @Override
                    public void prepareSuccessView(
                            LogoutOutputData outputData) {
                    }

                    @Override
                    public void prepareFailView(String message) {
                        fail("Unexpected logout failure: " + message);
                    }
                };

        private final RefreshInputBoundary refresh =
                inputData -> {
                    for (UUID userId : inputData.getUserIds()) {
                        boolean found = users.values().stream()
                                .anyMatch(user ->
                                        user.getUserId().equals(userId));

                        if (!found) {
                            throw new RefreshFailureException(
                                    "User not found.");
                        }
                    }
                };

        private final SignUpInteractor signUp =
                new SignUpInteractor(
                        new SignUpDataAccessInterface() {
                            @Override
                            public boolean existsByUsername(String username) {
                                return users.containsKey(username);
                            }

                            @Override
                            public UUID saveUser(User user) {
                                users.put(user.getUsername(), user);
                                return user.getUserId();
                            }
                        },
                        signUpPresenter,
                        hasher,
                        refresh);

        private final LoginInteractor login =
                new LoginInteractor(
                        users::get,
                        loginPresenter,
                        hasher,
                        refresh,
                        session);

        private final LogoutInteractor logout =
                new LogoutInteractor(session, logoutPresenter);
    }

    private static class LoginPresenterSpy
            implements LoginOutputBoundary {

        private String error;

        @Override
        public void prepareSuccessView(LoginOutputData outputData) {
            error = null;
        }

        @Override
        public void prepareFailView(String message) {
            error = message;
        }
    }
}
