package app_business_rules.logout;

import java.util.UUID;

/**
 * Defines the session operations required by logout.
 */
public interface SessionState {

    /**
     * Returns the currently authenticated user's ID.
     *
     * @return the current user ID, or null if logged out
     */
    UUID getCurrentUserId();

    /**
     * Clears the authenticated session.
     */
    /**
     * Establishes the authenticated session.
     *
     * @param userId the authenticated user's ID
     */
    void setCurrentUserId(UUID userId);

    void clear();
}
