package app_business_rules.login;

import app_business_rules.PasswordHasher;
import app_business_rules.logout.SessionState;
import app_business_rules.refresh.RefreshInputBoundary;
import app_business_rules.refresh.RefreshInputData;
import entities.users.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the login use case interactor.
 */
class LoginInteractorTest {

    @Test
    void testSuccessfulLogin() {
        TestContext context = new TestContext();
        context.execute("testuser", "password123");

        assertNotNull(context.presenter.successData);
        assertNull(context.presenter.errorMessage);
        assertEquals(context.user.getUserId(),
                context.presenter.successData.getUserId());
        assertEquals("testuser",
                context.presenter.successData.getUsername());
        assertEquals(1, context.refresh.callCount);
        assertEquals(context.user.getUserId(),
                context.session.getCurrentUserId());
        assertEquals(context.user.getUserId(),
                context.refresh.lastInputData.getUserIds().get(0));
    }

    @Test
    void testIncorrectPassword() {
        TestContext context = new TestContext();
        context.execute("testuser", "wrongpassword");

        assertFailedLogin(context, "Invalid username or password.");
    }

    @Test
    void testFailedLoginPreservesExistingSession() {
        TestContext context = new TestContext();
        UUID existingUserId = UUID.randomUUID();
        context.session.setCurrentUserId(existingUserId);

        context.execute("testuser", "wrongpassword");

        assertEquals("Invalid username or password.",
                context.presenter.errorMessage);
        assertNull(context.presenter.successData);
        assertEquals(0, context.refresh.callCount);
        assertEquals(existingUserId,
                context.session.getCurrentUserId());
    }

    @Test
    void testUnknownUsername() {
        TestContext context = new TestContext();
        context.execute("unknown", "password123");

        assertFailedLogin(context, "Invalid username or password.");
    }

    @Test
    void testMissingCredentials() {
        String[][] inputs = {
            {"", "password123"},
            {"testuser", ""},
            {"   ", "password123"},
            {"testuser", "   "},
            {null, "password123"},
            {"testuser", null}
        };

        for (String[] input : inputs) {
            TestContext context = new TestContext();
            context.execute(input[0], input[1]);
            assertFailedLogin(context,
                    "Username and password are required.");
        }
    }

    @Test
    void testNullInputData() {
        TestContext context = new TestContext();
        context.interactor.execute(null);

        assertFailedLogin(context,
                "Username and password are required.");
    }

    @Test
    void testRefreshFailure() {
        TestContext context = new TestContext();
        context.refresh.failOnRefresh = true;

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> context.execute("testuser", "password123"));

        assertEquals("Simulated refresh failure",
                exception.getMessage());
        assertNull(context.presenter.successData);
        assertEquals(1, context.refresh.callCount);
        assertNull(context.session.getCurrentUserId());
    }

    private static void assertFailedLogin(
            TestContext context, String expectedError) {
        assertEquals(expectedError, context.presenter.errorMessage);
        assertNull(context.presenter.successData);
        assertEquals(0, context.refresh.callCount);
        assertNull(context.session.getCurrentUserId());
    }

    private static class TestContext {
        private final User user = new User(
                UUID.randomUUID(),
                "testuser",
                "hashed_password123",
                "Test",
                "User",
                new ArrayList<UUID>());

        private final FakeLoginPresenter presenter =
                new FakeLoginPresenter();

        private final FakeRefresh refresh = new FakeRefresh();

        private final FakeSession session = new FakeSession();

        private final LoginInteractor interactor = new LoginInteractor(
                username -> user.getUsername().equals(username)
                        ? user : null,
                presenter,
                new FakePasswordHasher(),
                refresh,
                session);

        private void execute(String username, String password) {
            interactor.execute(new LoginInputData(username, password));
        }
    }

    private static class FakeLoginPresenter
            implements LoginOutputBoundary {

        private LoginOutputData successData;
        private String errorMessage;

        @Override
        public void prepareSuccessView(LoginOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private static class FakePasswordHasher implements PasswordHasher {

        @Override
        public String hash(String password) {
            return "hashed_" + password;
        }

        @Override
        public boolean verify(String password, String passwordHash) {
            return ("hashed_" + password).equals(passwordHash);
        }
    }

    private static class FakeSession implements SessionState {
        private UUID currentUserId;

        @Override
        public UUID getCurrentUserId() {
            return currentUserId;
        }

        @Override
        public void setCurrentUserId(UUID userId) {
            currentUserId = userId;
        }

        @Override
        public void clear() {
            currentUserId = null;
        }
    }

    private static class FakeRefresh implements RefreshInputBoundary {

        private RefreshInputData lastInputData;
        private int callCount;
        private boolean failOnRefresh;

        @Override
        public void execute(RefreshInputData inputData) {
            lastInputData = inputData;
            callCount++;

            if (failOnRefresh) {
                throw new IllegalStateException("Simulated refresh failure");
            }
        }
    }
}
