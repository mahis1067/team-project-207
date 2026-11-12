package interface_adapters.login;

import java.util.UUID;

/**
 * Stores the login result for the view.
 */
public class LoginViewModel {

    private UUID userId;
    private String username;
    private String errorMessage;
    private boolean successful;

    /**
     * Updates the state after successful login.
     *
     * @param userId the authenticated user's ID
     * @param username the authenticated username
     */
    public void setSuccess(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
        this.errorMessage = null;
        this.successful = true;
    }

    /**
     * Updates the state after failed login.
     *
     * @param errorMessage the failure message
     */
    public void setError(String errorMessage) {
        this.userId = null;
        this.username = null;
        this.errorMessage = errorMessage;
        this.successful = false;
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

    /**
     * Returns the latest login error.
     *
     * @return the error message, or null
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns whether login was successful.
     *
     * @return true if login succeeded
     */
    public boolean isSuccessful() {
        return successful;
    }
}
