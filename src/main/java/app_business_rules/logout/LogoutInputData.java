package app_business_rules.logout;

import java.util.UUID;

/**
 * Contains the identity of the user requesting logout.
 */
public class LogoutInputData {

    private final UUID userId;

    /**
     * Constructs logout input data.
     *
     * @param userId the current user's ID
     */
    public LogoutInputData(UUID userId) {
        this.userId = userId;
    }

    /**
     * Returns the user ID.
     *
     * @return the user ID
     */
    public UUID getUserId() {
        return userId;
    }
}
