package app;

import frameworks_and_drivers.view.ViewManager;
import interface_adapters.view_manager.ViewManagerModel;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import frameworks_and_drivers.view.LoginView;
import interface_adapters.login.LoginViewModel;
import interface_adapters.view_manager.ViewNames;
import frameworks_and_drivers.view.SignupView;
import interface_adapters.signup.SignUpViewModel;
import frameworks_and_drivers.view.DashboardView;
import interface_adapters.logout.LogoutViewModel;

/**
 * Builds the application: creates every view, wires every use case,
 * and returns the main window.
 */
public class AppBuilder {

    /** Default window width in pixels. */
    private static final int WINDOW_WIDTH = 960;
    /** Default window height in pixels. */
    private static final int WINDOW_HEIGHT = 640;

    /** Panel holding every screen, stacked like cards. */
    private final JPanel views = new JPanel();
    /** Layout that shows one screen at a time. */
    private final CardLayout cardLayout = new CardLayout();
    /** Model storing which screen is active. */
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();
    /** Switches screens when the model changes. */
    private final ViewManager viewManager;
    /** State shared by the login screen and its presenter. */
    private final LoginViewModel loginViewModel = new LoginViewModel();
    /** State shared by the sign-up screen and its presenter. */
    private final SignUpViewModel signUpViewModel = new SignUpViewModel();
    /** State shared by the dashboard and the logout presenter. */
    private final LogoutViewModel logoutViewModel = new LogoutViewModel();

    /** Sets up the card layout and the view manager. */
    public AppBuilder() {
        views.setLayout(cardLayout);
        viewManager = new ViewManager(views, cardLayout, viewManagerModel);
    }

    /**
     * Adds the login screen.
     *
     * @return this builder
     */
    public AppBuilder addLoginView() {
        LoginView loginView = new LoginView(loginViewModel, viewManagerModel);
        views.add(loginView, ViewNames.LOGIN);
        return this;
    }

    /**
     * Adds the sign-up screen.
     *
     * @return this builder
     */
    public AppBuilder addSignupView() {
        SignupView signupView =
                new SignupView(signUpViewModel, viewManagerModel);
        views.add(signupView, ViewNames.SIGNUP);
        return this;
    }

    /**
     * Adds the dashboard screen.
     *
     * @return this builder
     */
    public AppBuilder addDashboardView() {
        DashboardView dashboardView = new DashboardView(
                loginViewModel, logoutViewModel, viewManagerModel);
        views.add(dashboardView, ViewNames.DASHBOARD);
        return this;
    }

    // TODO (#14): addFriendsView

    // TODO (#14): addExpenseView

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
        viewManagerModel.setActiveView(ViewNames.LOGIN);
        JFrame frame = new JFrame("Budget Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(views);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLocationRelativeTo(null);
        return frame;
    }
}
