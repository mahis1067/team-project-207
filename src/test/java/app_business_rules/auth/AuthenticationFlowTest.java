package app_business_rules.auth;

import app_business_rules.PasswordHasher;
import app_business_rules.login.*;
import app_business_rules.logout.*;
import app_business_rules.refresh.*;
import entities.users.User;
import interface_adapters.session.InMemorySessionState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for login and logout sharing one session.
 */
class AuthenticationFlowTest {

    @Test
    void testLoginThenLogout() {
        TestContext context = new TestContext();

        context.login.execute(
                new LoginInputData("testuser", "password123"));

        assertEquals(context.user.getUserId(),
                context.session.getCurrentUserId());
        assertEquals(1, context.loginPresenter.successCount);
        assertEquals(1, context.refreshCount[0]);

        context.logout.execute(
                new LogoutInputData(context.user.getUserId()));

        assertNull(context.session.getCurrentUserId());
        assertEquals(1, context.logoutPresenter.successCount);
        assertEquals(context.user.getUserId(),
                context.logoutPresenter.loggedOutUserId);
    }

    @Test
    void testIncorrectPasswordDoesNotCreateSession() {
        TestContext context = new TestContext();

        context.login.execute(
                new LoginInputData("testuser", "incorrect"));

        assertNull(context.session.getCurrentUserId());
        assertEquals(0, context.loginPresenter.successCount);
        assertEquals("Invalid username or password.",
                context.loginPresenter.errorMessage);
        assertEquals(0, context.refreshCount[0]);
    }

    @Test
    void testLogoutWithoutLoginFails() {
        TestContext context = new TestContext();

        context.logout.execute(
                new LogoutInputData(context.user.getUserId()));

        assertNull(context.session.getCurrentUserId());
        assertEquals(0, context.logoutPresenter.successCount);
        assertEquals("No matching active session.",
                context.logoutPresenter.errorMessage);
    }

    @Test
    void testLoginLogoutAndLoginAgain() {
        TestContext context = new TestContext();

        context.login.execute(
                new LoginInputData("testuser", "password123"));
        context.logout.execute(
                new LogoutInputData(context.user.getUserId()));

        assertNull(context.session.getCurrentUserId());

        context.login.execute(
                new LoginInputData("testuser", "password123"));

        assertEquals(context.user.getUserId(),
                context.session.getCurrentUserId());
        assertEquals(2, context.loginPresenter.successCount);
        assertEquals(1, context.logoutPresenter.successCount);
        assertEquals(2, context.refreshCount[0]);
    }

    private static class TestContext {
        private final User user = new User(
                UUID.randomUUID(),
                "testuser",
                "hashed_password123",
                "Test",
                "User",
                new ArrayList<UUID>());

        private final InMemorySessionState session =
                new InMemorySessionState();

        private final LoginPresenterSpy loginPresenter =
                new LoginPresenterSpy();

        private final LogoutPresenterSpy logoutPresenter =
                new LogoutPresenterSpy();

        private final int[] refreshCount = {0};

        private final PasswordHasher hasher = new PasswordHasher() {
            @Override
            public String hash(String password) {
                return "hashed_" + password;
            }

            @Override
            public boolean verify(String password, String hash) {
                return ("hashed_" + password).equals(hash);
            }
        };

        private final RefreshInputBoundary refresh =
                new RefreshInputBoundary() {
                    @Override
                    public void execute(RefreshInputData data) {
                        refreshCount[0]++;
                    }
                };

        private final LoginInteractor login =
                new LoginInteractor(
                        username -> user.getUsername().equals(username)
                                ? user : null,
                        loginPresenter,
                        hasher,
                        refresh,
                        session);

        private final LogoutInteractor logout =
                new LogoutInteractor(session, logoutPresenter);
    }

    private static class LoginPresenterSpy
            implements LoginOutputBoundary {

        private int successCount;
        private String errorMessage;

        @Override
        public void prepareSuccessView(LoginOutputData outputData) {
            successCount++;
            errorMessage = null;
        }

        @Override
        public void prepareFailView(String message) {
            errorMessage = message;
        }
    }

    private static class LogoutPresenterSpy
            implements LogoutOutputBoundary {

        private int successCount;
        private UUID loggedOutUserId;
        private String errorMessage;

        @Override
        public void prepareSuccessView(LogoutOutputData outputData) {
            successCount++;
            loggedOutUserId = outputData.getUserId();
            errorMessage = null;
        }

        @Override
        public void prepareFailView(String message) {
            errorMessage = message;
        }
    }
}
