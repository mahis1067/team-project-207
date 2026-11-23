package interface_adapters.view_manager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Stores the name of the screen currently shown and notifies
 * listeners when it changes.
 */
public class ViewManagerModel {

    /** Property name fired when the active view changes. */
    public static final String ACTIVE_VIEW = "activeView";

    /** Notifies listeners when the active view changes. */
    private final PropertyChangeSupport support =
            new PropertyChangeSupport(this);
    /** Name of the screen currently shown. */
    private String activeView = "";

    /**
     * Returns the name of the active view.
     *
     * @return the active view name
     */
    public String getActiveView() {
        return activeView;
    }

    /**
     * Switches to a new view and notifies listeners.
     *
     * @param viewName the name of the view to show
     */
    public void setActiveView(String viewName) {
        String oldView = activeView;
        activeView = viewName;
        support.firePropertyChange(ACTIVE_VIEW, oldView, viewName);
    }

    /**
     * Registers a listener for view changes.
     *
     * @param listener the listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
