
package app_business_rules.signup;

import java.util.UUID;

/**
 * Contains the output data for a successful sign-up.
 */
public class SignUpOutputData {

    private final UUID userId;
    private final String username;

    /**
     * Constructs the sign-up output data.
     *
     * @param userId the unique identifier of the registered user
     * @param username the username of the registered user
     */
    public SignUpOutputData(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    /**
     * Returns the registered user's ID.
     *
     * @return the user ID
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * Returns the registered user's username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }
}
