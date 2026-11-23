package frameworks_and_drivers.view;

import interface_adapters.login.LoginController;
import interface_adapters.login.LoginViewModel;
import interface_adapters.view_manager.ViewManagerModel;
import interface_adapters.view_manager.ViewNames;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** Screen where an existing user logs in. */
public class LoginView extends JPanel implements PropertyChangeListener {

    /** Width of the login card in pixels. */
    private static final int CARD_WIDTH = 380;

    /** Holds the login result. */
    private final LoginViewModel viewModel;
    /** Used to switch screens. */
    private final ViewManagerModel viewManagerModel;
    /** Runs the login use case; set by AppBuilder. */
    private LoginController loginController;

    /** Username input. */
    private final JTextField usernameField = Theme.textField();
    /** Password input. */
    private final JPasswordField passwordField = Theme.passwordField();
    /** Shows login errors in red. */
    private final JLabel errorLabel =
            Theme.label(" ", Theme.SMALL, Theme.RED);

    /**
     * Builds the login screen.
     *
     * @param viewModel the login view model
     * @param viewManagerModel the model used to switch screens
     */
    public LoginView(LoginViewModel viewModel,
                     ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        viewModel.addPropertyChangeListener(this);

        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND);

        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = Theme.label("Welcome back", Theme.TITLE, Theme.TEXT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel subtitle = Theme.label("Log in to see who owes who.",
                Theme.BODY, Theme.MUTED);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        JButton loginButton = Theme.primaryButton("Log in");
        JButton signupButton = Theme.secondaryButton("Create an account");

        addRow(card, title);
        addRow(card, subtitle);
        card.add(Box.createVerticalStrut(Theme.PAD));
        addRow(card, new LabelledField("Username", usernameField));
        addRow(card, new LabelledField("Password", passwordField));
        addRow(card, errorLabel);
        addRow(card, loginButton);
        addRow(card, signupButton);

        card.setPreferredSize(new Dimension(
                CARD_WIDTH, card.getPreferredSize().height));
        add(card);

        loginButton.addActionListener(e -> logIn());
        passwordField.addActionListener(e -> logIn());
        signupButton.addActionListener(e ->
                viewManagerModel.setActiveView(ViewNames.SIGNUP));
    }

    /**
     * Sets the controller this view calls.
     *
     * @param loginController the login controller
     */
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    /**
     * Reacts to login success or failure.
     *
     * @param evt the change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (viewModel.isSuccessful()) {
            errorLabel.setText(" ");
            passwordField.setText("");
            viewManagerModel.setActiveView(ViewNames.DASHBOARD);
        } else {
            errorLabel.setText(viewModel.getErrorMessage());
        }
    }

    private void logIn() {
        if (loginController != null) {
            loginController.execute(usernameField.getText(),
                    new String(passwordField.getPassword()));
        }
    }

    private static void addRow(JPanel card, JComponent row) {
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, row.getPreferredSize().height));
        card.add(row);
        card.add(Box.createVerticalStrut(Theme.GAP));
    }
}
