package superbase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * Reads and writes users in the Supabase database.
 */
public class SupabaseUserDao {
    private final PostgresDataSource source;

    /**
     * @param source the database connection provider
     */
    public SupabaseUserDao(PostgresDataSource source) {
        this.source = source;
    }

    /** A row of the users table. */
    public record UserRow(UUID id, String username, String passwordHash,
                          String firstName, String lastName) { }

    /**
     * Inserts a user. The password must already be hashed.
     * @param username unique username
     * @param passwordHash hashed password
     * @param firstName first name
     * @param lastName last name
     * @return the generated user id
     * @throws SQLException if the insert fails (e.g. duplicate username)
     */
    public UUID save(String username, String passwordHash,
                     String firstName, String lastName) throws SQLException {
        String sql = "insert into users (username, password_hash, first_name, last_name) "
                + "values (?, ?, ?, ?) returning id";
        try (Connection c = source.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, firstName);
            ps.setString(4, lastName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getObject("id", UUID.class);
            }
        }
    }

    /**
     * @param username the username to look up
     * @return the user row, or empty if none exists
     * @throws SQLException on database error
     */
    public Optional<UserRow> findByUsername(String username) throws SQLException {
        String sql = "select id, username, password_hash, first_name, last_name "
                + "from users where username = ?";
        try (Connection c = source.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new UserRow(rs.getObject("id", UUID.class),
                        rs.getString("username"), rs.getString("password_hash"),
                        rs.getString("first_name"), rs.getString("last_name")));
            }
        }
    }

    /**
     * @param username the username to check
     * @return true if a user with that username exists
     * @throws SQLException on database error
     */
    public boolean existsByUsername(String username) throws SQLException {
        return findByUsername(username).isPresent();
    }

    /**
     * Deletes a user (cascades to their friendships, notifications, etc.).
     * @param id the user id
     * @throws SQLException on database error
     */
    public void deleteById(UUID id) throws SQLException {
        try (Connection c = source.getConnection();
             PreparedStatement ps = c.prepareStatement("delete from users where id = ?")) {
            ps.setObject(1, id);
            ps.executeUpdate();
        }
    }
}