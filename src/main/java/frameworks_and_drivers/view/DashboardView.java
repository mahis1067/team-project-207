package frameworks_and_drivers.view;

import interface_adapters.login.LoginViewModel;
import interface_adapters.logout.LogoutController;
import interface_adapters.logout.LogoutViewModel;
import interface_adapters.view_manager.ViewManagerModel;
import interface_adapters.view_manager.ViewNames;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Home screen shown after logging in. */
public class DashboardView extends JPanel implements PropertyChangeListener {

    /** Tells the dashboard who logged in. */
    private final LoginViewModel loginViewModel;
    /** Tells the dashboard when logout succeeds. */
    private final LogoutViewModel logoutViewModel;
    /** Used to switch screens. */
    private final ViewManagerModel viewManagerModel;
    /** Runs the logout use case; set by AppBuilder. */
    private LogoutController logoutController;
    /** ID of the logged-in user, needed to log out. */
    private UUID currentUserId;

    /** Greets the logged-in user. */
    private final JLabel welcomeLabel =
            Theme.label("Welcome back!", Theme.TITLE, Theme.TEXT);

    /**
     * Builds the dashboard.
     *
     * @param loginViewModel tells the dashboard who logged in
     * @param logoutViewModel tells the dashboard when logout succeeds
     * @param viewManagerModel the model used to switch screens
     */
    public DashboardView(LoginViewModel loginViewModel,
                         LogoutViewModel logoutViewModel,
                         ViewManagerModel viewManagerModel) {
        this.loginViewModel = loginViewModel;
        this.logoutViewModel = logoutViewModel;
        this.viewManagerModel = viewManagerModel;
        loginViewModel.addPropertyChangeListener(this);
        logoutViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        add(new NavigationBar(viewManagerModel, ViewNames.DASHBOARD),
                BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout(0, Theme.PAD));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(
                Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD));
        content.add(buildTop(), BorderLayout.NORTH);
        content.add(buildLists(), BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }

    /**
     * Sets the logout controller.
     *
     * @param logoutController the logout controller
     */
    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    /**
     * Updates the screen after login or logout.
     *
     * @param evt the change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == loginViewModel
                && loginViewModel.isSuccessful()) {
            currentUserId = loginViewModel.getUserId();
            welcomeLabel.setText(
                    "Welcome back, " + loginViewModel.getUsername() + "!");
        } else if (evt.getSource() == logoutViewModel
                && logoutViewModel.isSuccessful()) {
            currentUserId = null;
            viewManagerModel.setActiveView(ViewNames.LOGIN);
        }
    }

    private JPanel buildTop() {
        JButton addExpenseButton = Theme.primaryButton("+ Add expense");
        JButton logoutButton = Theme.secondaryButton("Log out");
        addExpenseButton.addActionListener(e ->
                viewManagerModel.setActiveView(ViewNames.EXPENSE));
        logoutButton.addActionListener(e -> logOut());

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(addExpenseButton);
        buttons.add(logoutButton);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(welcomeLabel, BorderLayout.WEST);
        header.add(buttons, BorderLayout.EAST);

        // TODO (view_dashboard use case): show the real totals.
        JPanel stats = new JPanel(new GridLayout(1, 3, Theme.PAD, 0));
        stats.setOpaque(false);
        stats.add(statCard("You owe", "$0.00", Theme.RED));
        stats.add(statCard("Owed to you", "$0.00", Theme.BLUE));
        stats.add(statCard("Net balance", "$0.00", Theme.TEXT));

        JPanel top = new JPanel(new BorderLayout(0, Theme.PAD));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(stats, BorderLayout.CENTER);
        return top;
    }

    private JPanel buildLists() {
        JPanel lists = new JPanel(new GridLayout(1, 2, Theme.PAD, 0));
        lists.setOpaque(false);
        // TODO (view_dashboard use case): list the real balances
        //  and expenses.
        lists.add(listCard("Who owes who", "No balances yet."));
        lists.add(listCard("Recent expenses", "No expenses yet."));
        return lists;
    }

    private void logOut() {
        if (logoutController != null && currentUserId != null) {
            logoutController.execute(currentUserId);
        }
    }

    private static JPanel statCard(String title, String amount,
                                   Color color) {
        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(Theme.label(title, Theme.SMALL, Theme.MUTED));
        card.add(Theme.label(amount, Theme.STAT, color));
        return card;
    }

    private static JPanel listCard(String title, String emptyText) {
        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, Theme.GAP));
        card.add(Theme.label(title, Theme.HEADING, Theme.TEXT),
                BorderLayout.NORTH);
        JLabel empty = Theme.label(emptyText, Theme.BODY, Theme.MUTED);
        empty.setVerticalAlignment(SwingConstants.TOP);
        card.add(empty, BorderLayout.CENTER);
        return card;
    }
}
