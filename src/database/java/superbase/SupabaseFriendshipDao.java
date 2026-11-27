package superbase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Reads and writes friendships in the Supabase database.
 */
public class SupabaseFriendshipDao {
    private final PostgresDataSource source;

    /** @param source the database connection provider */
    public SupabaseFriendshipDao(PostgresDataSource source) {
        this.source = source;
    }

    /**
     * Creates a sent friend request.
     * @param requestedBy sender id
     * @param sentTo receiver id
     * @return the friendship id
     * @throws SQLException if the pair already exists or ids are invalid
     */
    public UUID sendRequest(UUID requestedBy, UUID sentTo) throws SQLException {
        String sql = "insert into friendships "
                + "(requested_by, sent_to, status) values (?, ?, 'SENT_REQUEST') returning id";
        try (Connection c = source.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, requestedBy);
            ps.setObject(2, sentTo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getObject("id", UUID.class);
            }
        }
    }

    /**
     * Accepts a pending request.
     * @param friendshipId the friendship id
     * @throws SQLException on database error
     */
    public void accept(UUID friendshipId) throws SQLException {
        String sql = "update friendships set status = 'ADDED', added_at = now() where id = ?";
        try (Connection c = source.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, friendshipId);
            ps.executeUpdate();
        }
    }

    /**
     * Deletes a pending friendship request for the two users.
     * @param userId receiving user id
     * @param requesterId requesting user id
     * @return true only when one pending request was deleted
     * @throws SQLException on database error
     */
    public boolean deny(UUID userId, UUID requesterId)
            throws SQLException {
        String sql = "delete from friendships "
                + "where sent_to = ? and requested_by = ? "
                + "and status = 'SENT_REQUEST'";
        try (Connection c = source.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, userId);
            ps.setObject(2, requesterId);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * @param friendshipId the friendship id
     * @return the status string stored in the database
     * @throws SQLException on database error
     */
    public String getStatus(UUID friendshipId) throws SQLException {
        try (Connection c = source.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "select status from friendships where id = ?")) {
            ps.setObject(1, friendshipId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("status") : null;
            }
        }
    }

    /**
     * Finds the status of a friendship request between two users.
     * @param userId receiving user id
     * @param requesterId requesting user id
     * @return the stored status, or null if no request exists
     * @throws SQLException on database error
     */
    public String getStatusByUsers(UUID userId, UUID requesterId)
            throws SQLException {
        String sql = "select status from friendships "
                + "where sent_to = ? and requested_by = ?";
        try (Connection c = source.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, userId);
            ps.setObject(2, requesterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("status") : null;
            }
        }
    }
}