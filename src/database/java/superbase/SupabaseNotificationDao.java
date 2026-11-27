package superbase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
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
    public record NotificationRow(
            UUID id,
            UUID senderId,
            String type,
            String payload,
            UUID receiverId,
            boolean read,
            Instant createdAt) { }

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
        String sql = "select id, sender_id, receiver_id, type, "
                + "payload::text as payload, is_read, created_at "
                + "from notifications "
                + "where receiver_id = ? and is_read = false "
                + "order by created_at, id";
        List<NotificationRow> rows = new ArrayList<>();
        try (Connection c = source.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, receiverId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new NotificationRow(
                            rs.getObject("id", UUID.class),
                            rs.getObject("sender_id", UUID.class),
                            rs.getString("type"),
                            rs.getString("payload"),
                            rs.getObject("receiver_id", UUID.class),
                            rs.getBoolean("is_read"),
                            rs.getTimestamp("created_at").toInstant()));
                }
            }
        }
        return rows;
    }

    /**
     * Marks a notification read only for its intended receiver.
     *
     * @param notificationId the notification to mark as read
     * @param receiverId the authorized receiving user
     * @return true if one unread notification was updated
     * @throws SQLException on database error
     */
    public boolean markRead(UUID notificationId, UUID receiverId)
            throws SQLException {
        java.util.Objects.requireNonNull(notificationId,
                "notificationId");
        java.util.Objects.requireNonNull(receiverId,
                "receiverId");
        try (Connection c = source.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "update notifications set is_read = true "
                     + "where id = ? and receiver_id = ? "
                     + "and is_read = false")) {
            ps.setObject(1, notificationId);
            ps.setObject(2, receiverId);
            return ps.executeUpdate() == 1;
        }
    }
}
