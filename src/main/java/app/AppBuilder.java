package app;

import frameworks_and_drivers.view.ViewManager;
import interface_adapters.view_manager.ViewManagerModel;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Builds the application: creates every view, wires every use case,
 * and returns the main window.
 */
public class AppBuilder {

    private final JPanel views = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();
    private final ViewManager viewManager;

    /** Sets up the card layout and the view manager. */
    public AppBuilder() {
        views.setLayout(cardLayout);
        viewManager = new ViewManager(views, cardLayout, viewManagerModel);
    }

    // TODO (#14): addLoginView, addSignupView, addDashboardView,
    //             addFriendsView, addExpenseView
    // TODO (#14): addAuthUseCases (login, signup, logout)
    // TODO (teammates): add a method per use case, e.g.
    //   addExpenseUseCase, updateExpenseUseCase, confirmExpenseUseCase,
    //   addFriendUseCase, createGroupUseCase, manageMembersUseCase,
    //   viewDashboardUseCase, viewHistoryUseCase
    /**
     * Builds the main window.
     *
     * @return the application frame
     */
    public JFrame build() {
        JFrame frame = new JFrame("Budget Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(views);
        frame.setSize(500, 450);
        frame.setLocationRelativeTo(null);
        return frame;
    }
}