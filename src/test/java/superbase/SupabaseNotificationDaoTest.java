package superbase;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SupabaseNotificationDaoTest {

    private static final UUID SENDER = UUID.randomUUID();
    private static final UUID RECEIVER = UUID.randomUUID();
    private static final UUID NOTIFICATION = UUID.randomUUID();

    @Test
    void sendUsesPreparedStatementAndClosesResources()
            throws Exception {
        JdbcFixture jdbc = new JdbcFixture();
        SupabaseNotificationDao dao = jdbc.dao();

        dao.send(SENDER, RECEIVER, "NEW_FRIEND_REQUEST",
                "{\"senderId\":\"test\"}");

        assertTrue(jdbc.sql.contains("insert into notifications"));
        assertTrue(jdbc.sql.contains("?::jsonb"));
        assertEquals(SENDER, jdbc.parameters.get(1));
        assertEquals(RECEIVER, jdbc.parameters.get(2));
        assertEquals("NEW_FRIEND_REQUEST", jdbc.parameters.get(3));
        assertEquals("{\"senderId\":\"test\"}",
                jdbc.parameters.get(4));
        assertEquals(1, jdbc.updateCalls);
        assertTrue(jdbc.statementClosed);
        assertTrue(jdbc.connectionClosed);
    }

    @Test
    void unreadRowsIncludeAllNotificationFields()
            throws Exception {
        JdbcFixture jdbc = new JdbcFixture();
        Instant created = Instant.parse("2026-11-26T12:30:00Z");

        Map<String, Object> row = new HashMap<>();
        row.put("id", NOTIFICATION);
        row.put("sender_id", SENDER);
        row.put("receiver_id", RECEIVER);
        row.put("type", "NEW_EXPENSE_ADDED");
        row.put("payload", "{\"amount\":12.5}");
        row.put("is_read", false);
        row.put("created_at", Timestamp.from(created));
        jdbc.rows.add(row);

        var unread = jdbc.dao().getUnread(RECEIVER);

        assertEquals(1, unread.size());
        var notification = unread.get(0);

        assertEquals(NOTIFICATION, notification.id());
        assertEquals(SENDER, notification.senderId());
        assertEquals(RECEIVER, notification.receiverId());
        assertEquals("NEW_EXPENSE_ADDED", notification.type());
        assertEquals("{\"amount\":12.5}", notification.payload());
        assertFalse(notification.read());
        assertEquals(created, notification.createdAt());

        assertTrue(jdbc.sql.contains("receiver_id = ?"));
        assertTrue(jdbc.sql.contains("is_read = false"));
        assertTrue(jdbc.sql.contains("order by created_at, id"));
        assertEquals(RECEIVER, jdbc.parameters.get(1));
        assertTrue(jdbc.resultSetClosed);
        assertTrue(jdbc.statementClosed);
        assertTrue(jdbc.connectionClosed);
    }

    @Test
    void emptyInboxReturnsEmptyListAndClosesResources()
            throws Exception {
        JdbcFixture jdbc = new JdbcFixture();

        assertTrue(jdbc.dao().getUnread(RECEIVER).isEmpty());
        assertTrue(jdbc.resultSetClosed);
        assertTrue(jdbc.statementClosed);
        assertTrue(jdbc.connectionClosed);
    }

    @Test
    void markReadRestrictsReceiverAndReportsUpdatedRows()
            throws Exception {
        JdbcFixture jdbc = new JdbcFixture();
        jdbc.updatedRows = 1;

        assertTrue(jdbc.dao().markRead(NOTIFICATION, RECEIVER));

        assertTrue(jdbc.sql.contains("where id = ?"));
        assertTrue(jdbc.sql.contains("receiver_id = ?"));
        assertTrue(jdbc.sql.contains("is_read = false"));
        assertEquals(NOTIFICATION, jdbc.parameters.get(1));
        assertEquals(RECEIVER, jdbc.parameters.get(2));
        assertEquals(1, jdbc.updateCalls);
        assertTrue(jdbc.statementClosed);
        assertTrue(jdbc.connectionClosed);

        JdbcFixture noMatch = new JdbcFixture();
        noMatch.updatedRows = 0;
        assertFalse(noMatch.dao().markRead(NOTIFICATION, RECEIVER));
    }

    @Test
    void markReadRejectsNullIdentifiersBeforeConnecting() {
        JdbcFixture jdbc = new JdbcFixture();
        SupabaseNotificationDao dao = jdbc.dao();

        assertThrows(NullPointerException.class,
                () -> dao.markRead(null, RECEIVER));
        assertThrows(NullPointerException.class,
                () -> dao.markRead(NOTIFICATION, null));

        assertFalse(jdbc.connectionOpened);
    }

    @Test
    void databaseExceptionsArePropagatedAndResourcesClosed() {
        JdbcFixture jdbc = new JdbcFixture();
        jdbc.failure = new SQLException("Simulated JDBC failure");

        SQLException thrown = assertThrows(SQLException.class,
                () -> jdbc.dao().send(
                        SENDER, RECEIVER, "NEW_FRIEND_REQUEST", "{}"));

        assertEquals("Simulated JDBC failure", thrown.getMessage());
        assertTrue(jdbc.statementClosed);
        assertTrue(jdbc.connectionClosed);
    }

    private static final class JdbcFixture {
        String sql;
        final Map<Integer, Object> parameters = new HashMap<>();
        final List<Map<String, Object>> rows = new ArrayList<>();
        int updatedRows = 1;
        int updateCalls;
        int rowIndex = -1;
        boolean connectionOpened;
        boolean connectionClosed;
        boolean statementClosed;
        boolean resultSetClosed;
        SQLException failure;

        SupabaseNotificationDao dao() {
            PostgresDataSource source = new PostgresDataSource() {
                @Override
                public Connection getConnection() {
                    connectionOpened = true;
                    return connection();
                }
            };
            return new SupabaseNotificationDao(source);
        }

        Connection connection() {
            return proxy(Connection.class, (proxy, method, args) -> {
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
                    default -> unsupported(method.getName());
                };
            });
        }

        PreparedStatement statement() {
            return proxy(PreparedStatement.class,
                    (proxy, method, args) -> {
                        return switch (method.getName()) {
                            case "setObject", "setString" -> {
                                parameters.put(
                                        (Integer) args[0], args[1]);
                                yield null;
                            }
                            case "executeUpdate" -> {
                                updateCalls++;
                                if (failure != null) {
                                    throw failure;
                                }
                                yield updatedRows;
                            }
                            case "executeQuery" -> {
                                if (failure != null) {
                                    throw failure;
                                }
                                yield resultSet();
                            }
                            case "close" -> {
                                statementClosed = true;
                                yield null;
                            }
                            case "isClosed" -> statementClosed;
                            default -> unsupported(method.getName());
                        };
                    });
        }

        ResultSet resultSet() {
            return proxy(ResultSet.class, (proxy, method, args) -> {
                return switch (method.getName()) {
                    case "next" -> ++rowIndex < rows.size();
                    case "getObject", "getString",
                            "getBoolean", "getTimestamp" ->
                            rows.get(rowIndex).get((String) args[0]);
                    case "close" -> {
                        resultSetClosed = true;
                        yield null;
                    }
                    case "isClosed" -> resultSetClosed;
                    default -> unsupported(method.getName());
                };
            });
        }

        private static Object unsupported(String method) {
            throw new UnsupportedOperationException(
                    "Unexpected JDBC method: " + method);
        }

        @SuppressWarnings("unchecked")
        private static <T> T proxy(
                Class<T> type, InvocationHandler handler) {
            return (T) Proxy.newProxyInstance(
                    type.getClassLoader(),
                    new Class<?>[] {type},
                    handler);
        }
    }
}
