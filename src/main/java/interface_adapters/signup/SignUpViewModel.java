package interface_adapters.signup;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;

/**
 * Stores the state of the sign-up use case for the view.
 */
public class SignUpViewModel {

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
     * Updates the view model after successful sign-up.
     *
     * @param userId the registered user's ID
     * @param username the registered username
     */
    public void setSuccess(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
        this.errorMessage = null;
        this.successful = true;
        support.firePropertyChange(STATE, null, this);
    }

    /**
     * Updates the view model after failed sign-up.
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
     * Returns the registered user's ID.
     *
     * @return the user ID
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * Returns the registered username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the error message.
     *
     * @return the error message, or null
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns whether the latest sign-up succeeded.
     *
     * @return true if sign-up was successful
     */
    public boolean isSuccessful() {
        return successful;
    }
}
