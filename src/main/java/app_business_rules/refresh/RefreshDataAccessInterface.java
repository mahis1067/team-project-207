package app_business_rules.refresh;

import entities.users.User;
import java.util.UUID;

/**
 * Data access operations required by refresh.
 */
public interface RefreshDataAccessInterface {

    /**
     * Loads a user by ID.
     *
     * @param userId the user ID
     * @return the user, or null if not found
     */
    User getUserById(UUID userId);
}
