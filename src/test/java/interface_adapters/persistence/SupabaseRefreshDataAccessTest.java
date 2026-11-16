package interface_adapters.persistence;

import entities.users.User;
import org.junit.jupiter.api.Test;
import superbase.PostgresDataSource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SupabaseRefreshDataAccessTest {

    @Test
    void testFindExistingUser() {
        FakeDataSource source = new FakeDataSource();
        SupabaseRefreshDataAccess adapter =
                new SupabaseRefreshDataAccess(source);

        User user = adapter.getUserById(source.userId);

        assertNotNull(user);
        assertEquals(source.userId, user.getUserId());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashed_password", user.getPasswordHash());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals(source.userId, source.boundId);
    }

    @Test
    void testMissingUserReturnsNull() {
        FakeDataSource source = new FakeDataSource();
        source.userExists = false;

        SupabaseRefreshDataAccess adapter =
                new SupabaseRefreshDataAccess(source);

        assertNull(adapter.getUserById(source.userId));
    }

    @Test
    void testDatabaseFailurePreservesCause() {
        FakeDataSource source = new FakeDataSource();
        source.failConnection = true;

        SupabaseRefreshDataAccess adapter =
                new SupabaseRefreshDataAccess(source);

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> adapter.getUserById(source.userId));

        assertInstanceOf(SQLException.class, error.getCause());
    }

    @Test
    void testNullUserIdRejected() {
        FakeDataSource source = new FakeDataSource();

        SupabaseRefreshDataAccess adapter =
                new SupabaseRefreshDataAccess(source);

        assertThrows(
                IllegalArgumentException.class,
                () -> adapter.getUserById(null));

        assertFalse(source.connectionRequested);
    }

    @Test
    void testParameterizedUuidQuery() {
        FakeDataSource source = new FakeDataSource();

        SupabaseRefreshDataAccess adapter =
                new SupabaseRefreshDataAccess(source);

        adapter.getUserById(source.userId);

        assertNotNull(source.executedSql);
        assertTrue(source.executedSql.contains("WHERE id = ?"));
        assertEquals(source.userId, source.boundId);
        assertEquals(1, source.boundIndex);
    }

    private static class FakeDataSource extends PostgresDataSource {

        final UUID userId = UUID.randomUUID();

        boolean userExists = true;
        boolean failConnection;
        boolean connectionRequested;

        String executedSql;
        UUID boundId;
        int boundIndex;

        @Override
        public Connection getConnection() throws SQLException {
            connectionRequested = true;

            if (failConnection) {
                throw new SQLException("Simulated connection failure");
            }

            return proxy(Connection.class, (proxy, method, args) -> {
                switch (method.getName()) {
                    case "prepareStatement":
                        executedSql = (String) args[0];
                        return preparedStatement();
                    case "close":
                        return null;
                    default:
                        throw new UnsupportedOperationException(
                                method.getName());
                }
            });
        }

        private PreparedStatement preparedStatement() {
            return proxy(
                    PreparedStatement.class,
                    (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "setObject":
                                boundIndex = (Integer) args[0];
                                boundId = (UUID) args[1];
                                return null;
                            case "executeQuery":
                                return resultSet();
                            case "close":
                                return null;
                            default:
                                throw new UnsupportedOperationException(
                                        method.getName());
                        }
                    });
        }

        private ResultSet resultSet() {
            final boolean[] firstRow = {true};

            return proxy(ResultSet.class, (proxy, method, args) -> {
                switch (method.getName()) {
                    case "next":
                        if (firstRow[0] && userExists) {
                            firstRow[0] = false;
                            return true;
                        }
                        return false;
                    case "getObject":
                        return userId;
                    case "getString":
                        switch ((String) args[0]) {
                            case "username":
                                return "testuser";
                            case "password_hash":
                                return "hashed_password";
                            case "first_name":
                                return "Test";
                            case "last_name":
                                return "User";
                            default:
                                throw new IllegalArgumentException(
                                        (String) args[0]);
                        }
                    case "close":
                        return null;
                    default:
                        throw new UnsupportedOperationException(
                                method.getName());
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(
            Class<T> type,
            InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[]{type},
                handler);
    }
}
