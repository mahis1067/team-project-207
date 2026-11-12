package app_business_rules.auth;

import app_business_rules.PasswordHasher;
import app_business_rules.login.*;
import app_business_rules.refresh.*;
import entities.users.User;
import interface_adapters.session.InMemorySessionState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests error propagation from Refresh to Login.
 */
class LoginRefreshFailureTest {

    @Test
    void testRefreshFailurePreventsLoginSuccess() {
        User user = new User(
                UUID.randomUUID(),
                "testuser",
                "hashed_password123",
                "Test",
                "User",
                new ArrayList<UUID>());

        InMemorySessionState session = new InMemorySessionState();

        LoginPresenterSpy loginPresenter = new LoginPresenterSpy();
        RefreshPresenterSpy refreshPresenter = new RefreshPresenterSpy();

        PasswordHasher hasher = new PasswordHasher() {
            @Override
            public String hash(String password) {
                return "hashed_" + password;
            }

            @Override
            public boolean verify(String password, String hash) {
                return ("hashed_" + password).equals(hash);
            }
        };

        // Simulate a user disappearing from storage during refresh.
        RefreshInteractor refresh = new RefreshInteractor(
                userId -> null,
                refreshPresenter);

        LoginInteractor login = new LoginInteractor(
                username -> user.getUsername().equals(username)
                        ? user : null,
                loginPresenter,
                hasher,
                refresh,
                session);

        RefreshFailureException exception = assertThrows(
                RefreshFailureException.class,
                () -> login.execute(
                        new LoginInputData("testuser", "password123")));

        assertEquals("User not found.", exception.getMessage());
        assertEquals("User not found.", refreshPresenter.errorMessage);
        assertEquals(0, loginPresenter.successCount);
        assertNull(session.getCurrentUserId());
    }

    private static class LoginPresenterSpy
            implements LoginOutputBoundary {

        private int successCount;

        @Override
        public void prepareSuccessView(LoginOutputData outputData) {
            successCount++;
        }

        @Override
        public void prepareFailView(String message) {
            // Not used in this test.
        }
    }

    private static class RefreshPresenterSpy
            implements RefreshOutputBoundary {

        private String errorMessage;

        @Override
        public void prepareSuccessView(RefreshOutputData outputData) {
            // Not expected in this test.
        }

        @Override
        public void prepareFailView(String message) {
            errorMessage = message;
        }
    }
}
