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

    /** Default window width in pixels. */
    private static final int WINDOW_WIDTH = 500;
    /** Default window height in pixels. */
    private static final int WINDOW_HEIGHT = 450;

    /** Panel holding every screen, stacked like cards. */
    private final JPanel views = new JPanel();
    /** Layout that shows one screen at a time. */
    private final CardLayout cardLayout = new CardLayout();
    /** Model storing which screen is active. */
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();
    /** Switches screens when the model changes. */
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
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLocationRelativeTo(null);
        return frame;
    }
}
