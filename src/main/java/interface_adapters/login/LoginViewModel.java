package interface_adapters.login;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;

/**
 * Stores the login result for the view.
 */
public class LoginViewModel {

    /** Property name fired whenever the state changes. */
    public static final String STATE = "state";

    /** Notifies listeners when the state changes. */
    private final PropertyChangeSupport support =
            new PropertyChangeSupport(this);

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
        support.firePropertyChange(STATE, null, this);
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
        support.firePropertyChange(STATE, null, this);
    }

    /**
     * Registers a listener that is notified when the state changes.
     *
     * @param listener the listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
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

