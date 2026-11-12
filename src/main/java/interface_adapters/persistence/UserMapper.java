package interface_adapters.persistence;

import entities.users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Converts persisted user fields into domain entities.
 */
public final class UserMapper {

    private UserMapper() {
    }

    /**
     * Creates a user with an explicitly supplied friendship list.
     *
     * @param userId database-assigned user ID
     * @param username username
     * @param passwordHash stored password hash
     * @param firstName first name
     * @param lastName last name
     * @param friendships loaded friendship IDs
     * @return the mapped user entity
     */
    public static User toUser(
            UUID userId,
            String username,
            String passwordHash,
            String firstName,
            String lastName,
            List<UUID> friendships) {

        if (userId == null || friendships == null) {
            throw new IllegalArgumentException(
                    "User ID and friendships cannot be null.");
        }

        return new User(
                userId,
                username,
                passwordHash,
                firstName,
                lastName,
                new ArrayList<>(friendships));
    }
}
