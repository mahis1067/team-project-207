package app_business_rules.login;

import java.util.UUID;

/**
 * Contains the result of a successful login.
 */
public class LoginOutputData {

    private final UUID userId;
    private final String username;

    /**
     * Constructs login output data.
     *
     * @param userId the authenticated user's ID
     * @param username the authenticated username
     */
    public LoginOutputData(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    /**
     * Returns the authenticated user's ID.
     *
     * @return the user ID
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * Returns the authenticated username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }
}
