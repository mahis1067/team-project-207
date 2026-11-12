package app_business_rules.auth;

import app_business_rules.PasswordHasher;
import app_business_rules.refresh.*;
import app_business_rules.signup.*;
import entities.users.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests registration when the database assigns the user ID.
 */
class DatabaseAssignedIdFlowTest {

    @Test
    void testDatabaseIdUsedThroughoutRegistration() {
        Map<UUID, User> storedUsers = new HashMap<>();
        UUID databaseId = UUID.randomUUID();

        SignUpDataAccessInterface dao =
                new SignUpDataAccessInterface() {

                    @Override
                    public boolean existsByUsername(String username) {
                        return storedUsers.values().stream()
                                .anyMatch(user ->
                                        user.getUsername().equals(username));
                    }

                    @Override
                    public UUID saveUser(User user) {
                        User persistedUser = new User(
                                databaseId,
                                user.getUsername(),
                                user.getPasswordHash(),
                                user.getFirstName(),
                                user.getLastName(),
                                new ArrayList<UUID>());

                        storedUsers.put(databaseId, persistedUser);
                        return databaseId;
                    }
                };

        RefreshResult refreshResult = new RefreshResult();

        RefreshInteractor refresh = new RefreshInteractor(
                storedUsers::get,
                refreshResult);

        SignUpResult signUpResult = new SignUpResult();

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

        SignUpInteractor signUp = new SignUpInteractor(
                dao,
                signUpResult,
                hasher,
                refresh);

        signUp.execute(new SignUpInputData(
                "testuser", "password123", "Test", "User"));

        assertNotNull(signUpResult.outputData);
        assertEquals(databaseId,
                signUpResult.outputData.getUserId());

        assertNotNull(refreshResult.outputData);
        assertEquals(databaseId,
                refreshResult.outputData.getUsers()
                        .get(0).getUserId());

        assertEquals("testuser",
                storedUsers.get(databaseId).getUsername());
    }

    private static class SignUpResult
            implements SignUpOutputBoundary {

        private SignUpOutputData outputData;

        @Override
        public void prepareSuccessView(SignUpOutputData result) {
            outputData = result;
        }

        @Override
        public void prepareFailView(String message) {
            fail("SignUp failed: " + message);
        }
    }

    private static class RefreshResult
            implements RefreshOutputBoundary {

        private RefreshOutputData outputData;

        @Override
        public void prepareSuccessView(RefreshOutputData result) {
            outputData = result;
        }

        @Override
        public void prepareFailView(String message) {
            fail("Refresh failed: " + message);
        }
    }
}
