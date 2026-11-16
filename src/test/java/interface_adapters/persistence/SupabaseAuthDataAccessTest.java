package interface_adapters.persistence;

import entities.users.User;
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
