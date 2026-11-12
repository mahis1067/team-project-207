package interface_adapters.session;

import app_business_rules.logout.SessionState;

import java.util.UUID;

/**
 * Stores the currently authenticated user in memory.
 */
public class InMemorySessionState implements SessionState {

    private UUID currentUserId;

    /**
     * Returns the currently authenticated user's ID.
     *
     * @return the user ID, or null
     */
    @Override
    public UUID getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Establishes a session for a user.
     *
     * @param userId the authenticated user's ID
     */
    @Override
    public void setCurrentUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID cannot be null.");
        }
        currentUserId = userId;
    }

    /**
     * Clears the current session.
     */
    @Override
    public void clear() {
        currentUserId = null;
    }
}
