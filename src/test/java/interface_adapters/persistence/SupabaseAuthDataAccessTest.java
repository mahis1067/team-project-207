package interface_adapters.persistence;

import app_business_rules.PasswordHasher;
import app_business_rules.login.LoginInputData;
import app_business_rules.login.LoginInteractor;
import app_business_rules.login.LoginOutputBoundary;
import app_business_rules.login.LoginOutputData;
import app_business_rules.refresh.RefreshInputBoundary;
import entities.users.User;
import interface_adapters.session.InMemorySessionState;
import org.junit.jupiter.api.Test;
import superbase.PostgresDataSource;
import superbase.SupabaseUserDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SupabaseAuthDataAccessTest {

    @Test
    void testUsernameExists() {
        FakeUserDao dao = new FakeUserDao();
        dao.existingUsername = true;

        SupabaseAuthDataAccess adapter =
                new SupabaseAuthDataAccess(dao);

        assertTrue(adapter.existsByUsername("testuser"));
        assertFalse(adapter.existsByUsername("unknown"));
    }

    @Test
    void testSaveReturnsDatabaseId() {
        FakeUserDao dao = new FakeUserDao();
        SupabaseAuthDataAccess adapter =
                new SupabaseAuthDataAccess(dao);

        User user = new User(
                UUID.randomUUID(), "testuser", "hashed_password",
                "Test", "User", new ArrayList<>());

        UUID savedId = adapter.saveUser(user);

        assertEquals(dao.databaseId, savedId);
        assertEquals("testuser", dao.savedUsername);
        assertEquals("hashed_password", dao.savedHash);
    }

    @Test
    void testFindByUsernameMapsUserRow() {
        FakeUserDao dao = new FakeUserDao();
        SupabaseAuthDataAccess adapter =
                new SupabaseAuthDataAccess(dao);

        User user = adapter.getUserByUsername("testuser");

        assertNotNull(user);
        assertEquals(dao.databaseId, user.getUserId());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashed_password", user.getPasswordHash());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
    }

    /** Loads the database user through the adapter and completes the login use case. */
    @Test
    void loginUsesUserCreatedFromDatabaseRow() {
        FakeUserDao dao = new FakeUserDao();
        SupabaseAuthDataAccess adapter = new SupabaseAuthDataAccess(dao);
        InMemorySessionState session = new InMemorySessionState();
        LoginOutputData[] success = new LoginOutputData[1];
        String[] failure = new String[1];
        int[] refreshCount = {0};
        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData outputData) {
                success[0] = outputData;
            }

            @Override
            public void prepareFailView(String message) {
                failure[0] = message;
            }
        };
        PasswordHasher passwordHasher = new PasswordHasher() {
            @Override
            public String hash(String password) {
                return "hashed_" + password;
            }

            @Override
            public boolean verify(String password, String passwordHash) {
                return "password123".equals(password)
                        && "hashed_password".equals(passwordHash);
            }
        };
        RefreshInputBoundary refresh = data -> refreshCount[0]++;
        LoginInteractor interactor = new LoginInteractor(
                adapter, presenter, passwordHasher, refresh, session);

        interactor.execute(new LoginInputData("testuser", "password123"));

        assertNull(failure[0]);
        assertNotNull(success[0]);
        assertEquals(dao.databaseId, success[0].getUserId());
        assertEquals(dao.databaseId, session.getCurrentUserId());
        assertEquals(1, refreshCount[0]);
    }

    @Test
    void testUnknownUserReturnsNull() {
        SupabaseAuthDataAccess adapter =
                new SupabaseAuthDataAccess(new FakeUserDao());

        assertNull(adapter.getUserByUsername("unknown"));
    }

    @Test
    void testSQLExceptionPropagation() {
        FakeUserDao dao = new FakeUserDao();
        dao.fail = true;

        SupabaseAuthDataAccess adapter =
                new SupabaseAuthDataAccess(dao);

        IllegalStateException lookup = assertThrows(
                IllegalStateException.class,
                () -> adapter.getUserByUsername("testuser"));

        assertInstanceOf(SQLException.class, lookup.getCause());

        IllegalStateException exists = assertThrows(
                IllegalStateException.class,
                () -> adapter.existsByUsername("testuser"));

        assertInstanceOf(SQLException.class, exists.getCause());

        User user = new User(
                UUID.randomUUID(), "testuser", "hash",
                "Test", "User", new ArrayList<>());

        IllegalStateException save = assertThrows(
                IllegalStateException.class,
                () -> adapter.saveUser(user));

        assertInstanceOf(SQLException.class, save.getCause());
    }

    private static class FakeUserDao extends SupabaseUserDao {

        private final UUID databaseId = UUID.randomUUID();
        private boolean existingUsername;
        private boolean fail;
        private String savedUsername;
        private String savedHash;

        FakeUserDao() {
            super(new PostgresDataSource());
        }

        @Override
        public boolean existsByUsername(String username)
                throws SQLException {
            checkFailure();
            return existingUsername
                    && "testuser".equals(username);
        }

        @Override
        public UUID save(
                String username,
                String passwordHash,
                String firstName,
                String lastName) throws SQLException {
            checkFailure();
            savedUsername = username;
            savedHash = passwordHash;
            return databaseId;
        }

        @Override
        public Optional<UserRow> findByUsername(String username)
                throws SQLException {
            checkFailure();

            if (!"testuser".equals(username)) {
                return Optional.empty();
            }

            return Optional.of(new UserRow(
                    databaseId,
                    "testuser",
                    "hashed_password",
                    "Test",
                    "User"));
        }

        private void checkFailure() throws SQLException {
            if (fail) {
                throw new SQLException(
                        "Simulated database failure");
            }
        }
    }
}
