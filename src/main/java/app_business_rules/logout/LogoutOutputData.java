package app_business_rules.logout;

import java.util.UUID;

/**
 * Contains the result of a successful logout.
 */
public class LogoutOutputData {

    private final UUID userId;

    /**
     * Constructs logout output data.
     *
     * @param userId the logged-out user's ID
     */
    public LogoutOutputData(UUID userId) {
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
