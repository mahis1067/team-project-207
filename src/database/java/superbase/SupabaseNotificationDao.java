package superbase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Sends and reads notifications between users.
 */
public class SupabaseNotificationDao {
    private final PostgresDataSource source;

    /** @param source the database connection provider */
    public SupabaseNotificationDao(PostgresDataSource source) {
        this.source = source;
    }

    /** A row of the notifications table. */
    public record NotificationRow(UUID id, UUID senderId, String type, String payload) { }

    /**
     * Sends a notification to another user.
     * @param senderId sender id (may be null for system messages)
     * @param receiverId receiver id
     * @param type notification type, e.g. FRIEND_REQUEST
     * @param payloadJson a JSON string such as {"expenseId":"..."}
     * @throws SQLException on database error
     */
    public void send(UUID senderId, UUID receiverId, String type, String payloadJson)
            throws SQLException {
        String sql = "insert into notifications (sender_id, receiver_id, type, payload) "
                + "values (?, ?, ?, ?::jsonb)";
        try (Connection c = source.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, senderId);
            ps.setObject(2, receiverId);
            ps.setString(3, type);
            ps.setString(4, payloadJson);
            ps.executeUpdate();
        }
    }

    /**
     * @param receiverId the user whose inbox to read
     * @return unread notifications, oldest first
     * @throws SQLException on database error
     */
    public List<NotificationRow> getUnread(UUID receiverId) throws SQLException {
        String sql = "select id, sender_id, type, payload::text as payload from notifications "
                + "where receiver_id = ? and is_read = false order by created_at";
        List<NotificationRow> rows = new ArrayList<>();
        try (Connection c = source.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, receiverId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new NotificationRow(rs.getObject("id", UUID.class),
                            rs.getObject("sender_id", UUID.class),
                            rs.getString("type"), rs.getString("payload")));
                }
            }
        }
        return rows;
    }

    /**
     * @param notificationId the notification to mark as read
     * @throws SQLException on database error
     */
    public void markRead(UUID notificationId) throws SQLException {
        try (Connection c = source.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "update notifications set is_read = true where id = ?")) {
            ps.setObject(1, notificationId);
            ps.executeUpdate();
        }
    }
}
