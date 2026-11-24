package interface_adapters.logout;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;

/**
 * Stores the result of a logout operation.
 */
public class LogoutViewModel {

    /** Property name fired whenever the state changes. */
    public static final String STATE = "state";

    /** Notifies listeners when the state changes. */
    private final PropertyChangeSupport support =
            new PropertyChangeSupport(this);

    private UUID loggedOutUserId;
    private String errorMessage;
    private boolean successful;

    public void setSuccess(UUID userId) {
        loggedOutUserId = userId;
        errorMessage = null;
        successful = true;
        support.firePropertyChange(STATE, null, this);
    }

    public void setError(String message) {
        loggedOutUserId = null;
        errorMessage = message;
        successful = false;
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
