package interface_adapters.logout;

import java.util.UUID;

/**
 * Stores the result of a logout operation.
 */
public class LogoutViewModel {

    private UUID loggedOutUserId;
    private String errorMessage;
    private boolean successful;

    public void setSuccess(UUID userId) {
        loggedOutUserId = userId;
        errorMessage = null;
        successful = true;
    }

    public void setError(String message) {
        loggedOutUserId = null;
        errorMessage = message;
        successful = false;
    }

    public UUID getLoggedOutUserId() {
        return loggedOutUserId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
