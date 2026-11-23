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
import frameworks_and_drivers.view.FriendsView;
import frameworks_and_drivers.view.ExpenseView;
import app_business_rules.PasswordHasher;
import app_business_rules.login.LoginInteractor;
import app_business_rules.logout.LogoutInteractor;
import app_business_rules.logout.SessionState;
import app_business_rules.refresh.RefreshInputBoundary;
import app_business_rules.refresh.RefreshInteractor;
import app_business_rules.signup.SignUpInteractor;
import frameworks_and_drivers.security.Pbkdf2PasswordHasher;
import interface_adapters.login.LoginController;
import interface_adapters.login.LoginPresenter;
import interface_adapters.logout.LogoutController;
import interface_adapters.logout.LogoutPresenter;
import interface_adapters.persistence.SupabaseAuthDataAccess;
import interface_adapters.persistence.SupabaseRefreshDataAccess;
import interface_adapters.refresh.RefreshPresenter;
import interface_adapters.refresh.RefreshViewModel;
import interface_adapters.session.InMemorySessionState;
import interface_adapters.signup.SignUpController;
import interface_adapters.signup.SignUpPresenter;
import superbase.PostgresDataSource;
import superbase.SupabaseUserDao;

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
    /** Remembers who is logged in, shared by login and logout. */
    private final SessionState sessionState = new InMemorySessionState();
    /** The login screen, kept so its controller can be set. */
    private LoginView loginView;
    /** The sign-up screen, kept so its controller can be set. */
    private SignupView signupView;
    /** The dashboard, kept so its logout controller can be set. */
    private DashboardView dashboardView;

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
        loginView = new LoginView(loginViewModel, viewManagerModel);
        views.add(loginView, ViewNames.LOGIN);
        return this;
    }

    /**
     * Adds the sign-up screen.
     *
     * @return this builder
     */
    public AppBuilder addSignupView() {
        signupView = new SignupView(signUpViewModel, viewManagerModel);
        views.add(signupView, ViewNames.SIGNUP);
        return this;
    }

    /**
     * Adds the dashboard screen.
     *
     * @return this builder
     */
    public AppBuilder addDashboardView() {
        dashboardView = new DashboardView(loginViewModel, logoutViewModel, viewManagerModel);
        views.add(dashboardView, ViewNames.DASHBOARD);
        return this;
    }

    /**
     * Adds the friends screen.
     *
     * @return this builder
     */
    public AppBuilder addFriendsView() {
        views.add(new FriendsView(viewManagerModel), ViewNames.FRIENDS);
        return this;
    }

    /**
     * Adds the add expense screen.
     *
     * @return this builder
     */
    public AppBuilder addExpenseView() {
        views.add(new ExpenseView(viewManagerModel), ViewNames.EXPENSE);
        return this;
    }

    /**
     * Connects log in, sign up and log out to their use cases.
     * Call this after the views are added.
     *
     * @return this builder
     */
    public AppBuilder addAuthUseCases() {
        PostgresDataSource source = new PostgresDataSource();
        SupabaseAuthDataAccess authDataAccess =
                new SupabaseAuthDataAccess(new SupabaseUserDao(source));
        PasswordHasher hasher = new Pbkdf2PasswordHasher();
        RefreshInputBoundary refresh = new RefreshInteractor(
                new SupabaseRefreshDataAccess(source),
                new RefreshPresenter(new RefreshViewModel()));

        loginView.setLoginController(new LoginController(
                new LoginInteractor(authDataAccess,
                        new LoginPresenter(loginViewModel),
                        hasher, refresh, sessionState)));

        signupView.setSignUpController(new SignUpController(
                new SignUpInteractor(authDataAccess,
                        new SignUpPresenter(signUpViewModel),
                        hasher, refresh)));

        dashboardView.setLogoutController(new LogoutController(
                new LogoutInteractor(sessionState,
                        new LogoutPresenter(logoutViewModel))));
        return this;
    }

    // TODO (teammates): add a method per use case, e.g.
    //   addExpenseUseCase, updateExpenseUseCase, confirmExpenseUseCase,
    //   addFriendUseCase, viewDashboardUseCase, viewHistoryUseCase

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
