package frameworks_and_drivers.view;

import interface_adapters.view_manager.ViewManagerModel;
import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

/**
 * Listens to the ViewManagerModel and shows the matching screen.
 */
public class ViewManager implements PropertyChangeListener {

    private final JPanel views;
    private final CardLayout cardLayout;

    /**
     * Constructs the view manager.
     *
     * @param views the panel holding every screen
     * @param cardLayout the layout used to switch screens
     * @param viewManagerModel the model to listen to
     */
    public ViewManager(JPanel views, CardLayout cardLayout,
                       ViewManagerModel viewManagerModel) {
        this.views = views;
        this.cardLayout = cardLayout;
        viewManagerModel.addPropertyChangeListener(this);
    }

    /**
     * Shows the new active view.
     *
     * @param evt the change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ViewManagerModel.ACTIVE_VIEW.equals(evt.getPropertyName())) {
            cardLayout.show(views, (String) evt.getNewValue());
        }
    }
}