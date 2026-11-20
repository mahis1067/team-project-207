package interface_adapters.persistence;

import app_business_rules.refresh.RefreshDataAccessInterface;
import entities.users.User;
import superbase.PostgresDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.UUID;

/**
 * Loads users from Supabase for the Refresh use case.
 */
public class SupabaseRefreshDataAccess
        implements RefreshDataAccessInterface {

    private static final String FIND_USER_BY_ID =
            "SELECT id, username, password_hash, first_name, last_name "
                    + "FROM users WHERE id = ?";

    private final PostgresDataSource source;

    public SupabaseRefreshDataAccess(PostgresDataSource source) {
        this.source = source;
    }

    @Override
    public User getUserById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID cannot be null.");
        }

        try (Connection connection = source.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(FIND_USER_BY_ID)) {

            statement.setObject(1, userId);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                return UserMapper.toUser(
                        result.getObject("id", UUID.class),
                        result.getString("username"),
                        result.getString("password_hash"),
                        result.getString("first_name"),
                        result.getString("last_name"),
                        Collections.emptyList());
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to refresh user from database.",
                    exception);
        }
    }
}
