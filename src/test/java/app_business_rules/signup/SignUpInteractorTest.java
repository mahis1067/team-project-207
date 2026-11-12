
package app_business_rules.signup;

import org.junit.jupiter.api.Test;

import entities.users.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import app_business_rules.PasswordHasher;

import app_business_rules.refresh.RefreshInputBoundary;
import app_business_rules.refresh.RefreshInputData;
import app_business_rules.refresh.RefreshFailureException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the sign-up use case interactor.
 */
class SignUpInteractorTest {

    /**
     * Verifies that a valid user can sign up successfully.
     */
    @Test
    void testSuccessfulSignUp() {
        FakeSignUpDataAccess dataAccess = new FakeSignUpDataAccess();
        FakeSignUpPresenter presenter = new FakeSignUpPresenter();
        FakePasswordHasher passwordHasher = new FakePasswordHasher();
        FakeRefresh refresh = new FakeRefresh();

        SignUpInteractor interactor = new SignUpInteractor(
                dataAccess, presenter, passwordHasher, refresh);

        SignUpInputData inputData = new SignUpInputData(
                "testuser", "password123", "Test", "User");

        interactor.execute(inputData);

        User savedUser = dataAccess.users.get("testuser");

        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("hashed_password123", savedUser.getPasswordHash());
        assertEquals("Test", savedUser.getFirstName());
        assertEquals("User", savedUser.getLastName());

        assertNotNull(presenter.successData);
        assertNull(presenter.errorMessage);
        assertEquals(savedUser.getUserId(),
                presenter.successData.getUserId());
        assertEquals("testuser",
                presenter.successData.getUsername());

        assertEquals(1, refresh.callCount);
        assertNotNull(refresh.lastInputData);
        assertEquals(1, refresh.lastInputData.getUserIds().size());
        assertEquals(savedUser.getUserId(),
                refresh.lastInputData.getUserIds().get(0));
    }

    @Test
    void testUsesDatabaseAssignedUserId() {
        FakeSignUpDataAccess dataAccess = new FakeSignUpDataAccess();
        dataAccess.databaseAssignedId = UUID.randomUUID();

        FakeSignUpPresenter presenter = new FakeSignUpPresenter();
        FakeRefresh refresh = new FakeRefresh();

        SignUpInteractor interactor = new SignUpInteractor(
                dataAccess, presenter, new FakePasswordHasher(), refresh);

        interactor.execute(new SignUpInputData(
                "testuser", "password123", "Test", "User"));

        User suppliedUser = dataAccess.users.get("testuser");

        assertNotNull(suppliedUser);
        assertNotNull(presenter.successData);
        org.junit.jupiter.api.Assertions.assertNotEquals(
                suppliedUser.getUserId(), dataAccess.databaseAssignedId);

        assertEquals(dataAccess.databaseAssignedId,
                presenter.successData.getUserId());
        assertEquals(dataAccess.databaseAssignedId,
                refresh.lastInputData.getUserIds().get(0));
    }

    /**
     * Verifies that registration fails for an existing username.
     */
    @Test
    void testDuplicateUsername() {
        FakeSignUpDataAccess dataAccess = new FakeSignUpDataAccess();
        FakeSignUpPresenter presenter = new FakeSignUpPresenter();
        FakePasswordHasher passwordHasher = new FakePasswordHasher();
        FakeRefresh refresh = new FakeRefresh();

        SignUpInteractor interactor = new SignUpInteractor(
                dataAccess, presenter, passwordHasher, refresh);

        SignUpInputData firstInput = new SignUpInputData(
                "testuser", "password123", "First", "User");
        interactor.execute(firstInput);

        User originalUser = dataAccess.users.get("testuser");
        assertNotNull(originalUser);

        SignUpInputData duplicateInput = new SignUpInputData(
                "testuser", "anotherPassword", "Second", "User");
        interactor.execute(duplicateInput);

        assertEquals("Username already exists.", presenter.errorMessage);
        assertEquals(1, dataAccess.users.size());
        assertEquals(originalUser, dataAccess.users.get("testuser"));
        assertEquals(1, refresh.callCount);
    }

    /**
     * Verifies that sign-up fails when the username is empty.
     */
    @Test
    void testEmptyUsername() {
        FakeSignUpDataAccess dataAccess = new FakeSignUpDataAccess();
        FakeSignUpPresenter presenter = new FakeSignUpPresenter();
        FakePasswordHasher passwordHasher = new FakePasswordHasher();
        FakeRefresh refresh = new FakeRefresh();

        SignUpInteractor interactor = new SignUpInteractor(
                dataAccess, presenter, passwordHasher, refresh);

        SignUpInputData inputData = new SignUpInputData(
                "", "password123", "Test", "User");

        interactor.execute(inputData);

        assertEquals("All fields are required.", presenter.errorMessage);
        assertNull(presenter.successData);
        assertEquals(0, dataAccess.users.size());
        assertEquals(0, refresh.callCount);
    }

    /**
     * Verifies that other required fields cannot be empty.
     */
    @Test
    void testOtherEmptyFields() {
        String[][] invalidInputs = {
                {"testuser", "", "Test", "User"},
                {"testuser", "password123", "", "User"},
                {"testuser", "password123", "Test", ""}
        };

        for (String[] fields : invalidInputs) {
            FakeSignUpDataAccess dataAccess = new FakeSignUpDataAccess();
            FakeSignUpPresenter presenter = new FakeSignUpPresenter();
            FakePasswordHasher passwordHasher = new FakePasswordHasher();
            FakeRefresh refresh = new FakeRefresh();

            SignUpInteractor interactor = new SignUpInteractor(
                    dataAccess, presenter, passwordHasher, refresh);

            SignUpInputData inputData = new SignUpInputData(
                    fields[0], fields[1], fields[2], fields[3]);

            interactor.execute(inputData);

            assertEquals("All fields are required.", presenter.errorMessage);
            assertNull(presenter.successData);
            assertEquals(0, dataAccess.users.size());
            assertEquals(0, refresh.callCount);
        }
    }

    /**
     * Verifies that null input and whitespace-only fields are rejected.
     */
    @Test
    void testNullAndWhitespaceInputs() {
        SignUpInputData[] invalidInputs = {
                null,
                new SignUpInputData("   ", "password123", "Test", "User"),
                new SignUpInputData("testuser", "   ", "Test", "User"),
                new SignUpInputData("testuser", "password123", "   ", "User"),
                new SignUpInputData("testuser", "password123", "Test", "   "),
                new SignUpInputData(null, "password123", "Test", "User"),
                new SignUpInputData("testuser", null, "Test", "User"),
                new SignUpInputData("testuser", "password123", null, "User"),
                new SignUpInputData("testuser", "password123", "Test", null)
        };

        for (SignUpInputData inputData : invalidInputs) {
            FakeSignUpDataAccess dataAccess = new FakeSignUpDataAccess();
            FakeSignUpPresenter presenter = new FakeSignUpPresenter();
            FakePasswordHasher passwordHasher = new FakePasswordHasher();
            FakeRefresh refresh = new FakeRefresh();

            SignUpInteractor interactor = new SignUpInteractor(
                    dataAccess, presenter, passwordHasher, refresh);

            interactor.execute(inputData);

            assertEquals("All fields are required.", presenter.errorMessage);
            assertNull(presenter.successData);
            assertEquals(0, dataAccess.users.size());
            assertEquals(0, refresh.callCount);
        }
    }

    /**
     * Verifies that a database save failure does not report success.
     */
    @Test
    void testDatabaseSaveFailure() {
        FakeSignUpDataAccess dataAccess = new FakeSignUpDataAccess();
        dataAccess.failOnSave = true;

        FakeSignUpPresenter presenter = new FakeSignUpPresenter();
        FakePasswordHasher passwordHasher = new FakePasswordHasher();
        FakeRefresh refresh = new FakeRefresh();

        SignUpInteractor interactor = new SignUpInteractor(
                dataAccess, presenter, passwordHasher, refresh);

        SignUpInputData inputData = new SignUpInputData(
                "testuser", "password123", "Test", "User");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> interactor.execute(inputData));

        assertEquals("Simulated database failure", exception.getMessage());
        assertEquals(0, dataAccess.users.size());
        assertNull(presenter.successData);
        assertEquals(0, refresh.callCount);
    }

    /**
     * Verifies that a refresh failure occurs after the user is saved.
     */
    @Test
    void testRefreshFailure() {
        FakeSignUpDataAccess dataAccess = new FakeSignUpDataAccess();
        FakeSignUpPresenter presenter = new FakeSignUpPresenter();
        FakePasswordHasher passwordHasher = new FakePasswordHasher();
        FakeRefresh refresh = new FakeRefresh();
        refresh.failOnRefresh = true;

        SignUpInteractor interactor = new SignUpInteractor(
                dataAccess, presenter, passwordHasher, refresh);

        SignUpInputData inputData = new SignUpInputData(
                "testuser", "password123", "Test", "User");

        interactor.execute(inputData);

        assertEquals(
                "Account created, but data refresh failed. "
                + "Please sign in again.",
                presenter.errorMessage);

        User savedUser = dataAccess.users.get("testuser");
        assertNotNull(savedUser);
        assertEquals(1, dataAccess.users.size());

        assertEquals(1, refresh.callCount);
        assertNotNull(refresh.lastInputData);
        assertEquals(savedUser.getUserId(),
                refresh.lastInputData.getUserIds().get(0));

        assertNull(presenter.successData);
    }

    @Test
    void testNullDatabaseAssignedUserId() {
        FakeSignUpDataAccess dataAccess = new FakeSignUpDataAccess();
        dataAccess.returnNullId = true;

        FakeSignUpPresenter presenter = new FakeSignUpPresenter();
        FakeRefresh refresh = new FakeRefresh();

        SignUpInteractor interactor = new SignUpInteractor(
                dataAccess, presenter, new FakePasswordHasher(), refresh);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> interactor.execute(new SignUpInputData(
                        "testuser", "password123", "Test", "User")));

        assertEquals("Data access returned a null user ID.",
                exception.getMessage());
        assertNull(presenter.successData);
        assertEquals(0, refresh.callCount);
    }

    /**
     * In-memory data access implementation for sign-up tests.
     */
    private static class FakeSignUpDataAccess
            implements SignUpDataAccessInterface {

        private final Map<String, User> users = new HashMap<>();

        private boolean failOnSave;
        private boolean returnNullId;
        private UUID databaseAssignedId;

        @Override
        public boolean existsByUsername(String username) {
            return users.containsKey(username);
        }

        @Override
        public UUID saveUser(User user) {
            if (failOnSave) {
                throw new IllegalStateException("Simulated database failure");
            }
            users.put(user.getUsername(), user);
            if (returnNullId) {
                return null;
            }
            return databaseAssignedId != null
                    ? databaseAssignedId : user.getUserId();
        }

    }

    /**
     * Records sign-up results for testing.
     */
    private static class FakeSignUpPresenter
            implements SignUpOutputBoundary {

        private SignUpOutputData successData;
        private String errorMessage;

        @Override
        public void prepareSuccessView(SignUpOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    /**
     * Fake password hasher for sign-up tests.
     */
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

    /**
     * Records refresh requests for sign-up tests.
     */
    private static class FakeRefresh implements RefreshInputBoundary {

        private RefreshInputData lastInputData;
        private int callCount;

        private boolean failOnRefresh;


        @Override
        public void execute(RefreshInputData inputData) {
            this.lastInputData = inputData;
            this.callCount++;

            if (failOnRefresh) {
                throw new RefreshFailureException(
                        "Simulated refresh failure");
            }
        }

    }
}
