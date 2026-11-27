package superbase;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Tests conditional friendship denial without a live database. */
class SupabaseFriendshipDenyDaoTest {

    @Test
    void pendingRequestDeletionBindsUsersAndChecksStatus()
            throws Exception {
        Fixture fixture = new Fixture();
        UUID receiver = UUID.randomUUID();
        UUID requester = UUID.randomUUID();

        assertTrue(fixture.dao().deny(receiver, requester));

        assertTrue(fixture.sql.contains("delete from friendships"));
        assertTrue(fixture.sql.contains("sent_to = ?"));
        assertTrue(fixture.sql.contains("requested_by = ?"));
        assertTrue(fixture.sql.contains("status = 'SENT_REQUEST'"));
        assertEquals(receiver, fixture.parameters.get(1));
        assertEquals(requester, fixture.parameters.get(2));
        assertTrue(fixture.statementClosed);
        assertTrue(fixture.connectionClosed);
    }

    @Test
    void deletionOfZeroRowsReportsFalse() throws Exception {
        Fixture fixture = new Fixture();
        fixture.updatedRows = 0;

        assertFalse(fixture.dao().deny(
                UUID.randomUUID(), UUID.randomUUID()));

        assertTrue(fixture.statementClosed);
        assertTrue(fixture.connectionClosed);
    }

    @Test
    void sqlFailurePropagatesAndClosesResources() {
        Fixture fixture = new Fixture();
        fixture.failure = new SQLException("Simulated failure");

        SQLException error = assertThrows(SQLException.class,
                () -> fixture.dao().deny(
                        UUID.randomUUID(), UUID.randomUUID()));

        assertEquals("Simulated failure", error.getMessage());
        assertTrue(fixture.statementClosed);
        assertTrue(fixture.connectionClosed);
    }

    private static final class Fixture {
        String sql;
        final Map<Integer, Object> parameters = new HashMap<>();
        int updatedRows = 1;
        SQLException failure;
        boolean statementClosed;
        boolean connectionClosed;

        SupabaseFriendshipDao dao() {
            PostgresDataSource source = new PostgresDataSource() {
                @Override
                public Connection getConnection() {
                    return connection();
                }
            };
            return new SupabaseFriendshipDao(source);
        }

        Connection connection() {
            return (Connection) Proxy.newProxyInstance(
                    Connection.class.getClassLoader(),
                    new Class<?>[] {Connection.class},
                    (proxy, method, args) -> {
                        return switch (method.getName()) {
                            case "prepareStatement" -> {
                                sql = (String) args[0];
                                yield statement();
                            }
                            case "close" -> {
                                connectionClosed = true;
                                yield null;
                            }
                            case "isClosed" -> connectionClosed;
                            default -> throw new UnsupportedOperationException(
                                    method.getName());
                        };
                    });
        }

        PreparedStatement statement() {
            return (PreparedStatement) Proxy.newProxyInstance(
                    PreparedStatement.class.getClassLoader(),
                    new Class<?>[] {PreparedStatement.class},
                    (proxy, method, args) -> {
                        return switch (method.getName()) {
                            case "setObject" -> {
                                parameters.put((Integer) args[0], args[1]);
                                yield null;
                            }
                            case "executeUpdate" -> {
                                if (failure != null) {
                                    throw failure;
                                }
                                yield updatedRows;
                            }
                            case "close" -> {
                                statementClosed = true;
                                yield null;
                            }
                            case "isClosed" -> statementClosed;
                            default -> throw new UnsupportedOperationException(
                                    method.getName());
                        };
                    });
        }
    }
}
