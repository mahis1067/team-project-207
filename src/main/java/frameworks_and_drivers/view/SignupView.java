package frameworks_and_drivers.view;

import interface_adapters.signup.SignUpController;
import interface_adapters.signup.SignUpViewModel;
import interface_adapters.view_manager.ViewManagerModel;
import interface_adapters.view_manager.ViewNames;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** Screen where a new user creates an account. */
public class SignupView extends JPanel implements PropertyChangeListener {

    /** Width of the sign-up card in pixels. */
    private static final int CARD_WIDTH = 440;

    /** Holds the sign-up result. */
    private final SignUpViewModel viewModel;
    /** Used to switch screens. */
    private final ViewManagerModel viewManagerModel;
    /** Runs the sign-up use case; set by AppBuilder. */
    private SignUpController signUpController;

    /** First name input. */
    private final JTextField firstNameField = Theme.textField();
    /** Last name input. */
    private final JTextField lastNameField = Theme.textField();
    /** Username input. */
    private final JTextField usernameField = Theme.textField();
    /** Password input. */
    private final JPasswordField passwordField = Theme.passwordField();
    /** Shows sign-up errors in red. */
    private final JLabel errorLabel =
            Theme.label(" ", Theme.SMALL, Theme.RED);

    /**
     * Builds the sign-up screen.
     *
     * @param viewModel the sign-up view model
     * @param viewManagerModel the model used to switch screens
     */
    public SignupView(SignUpViewModel viewModel,
                      ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        viewModel.addPropertyChangeListener(this);

        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND);

        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = Theme.label("Create an account",
                Theme.TITLE, Theme.TEXT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel subtitle = Theme.label(
                "Save your expenses and split them with friends.",
                Theme.BODY, Theme.MUTED);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel nameRow = new JPanel(new GridLayout(1, 2, Theme.GAP, 0));
        nameRow.setOpaque(false);
        nameRow.add(new LabelledField("First name", firstNameField));
        nameRow.add(new LabelledField("Last name", lastNameField));

        JButton signupButton = Theme.primaryButton("Sign up");
        JButton backButton = Theme.secondaryButton("Back to log in");

        addRow(card, title);
        addRow(card, subtitle);
        card.add(Box.createVerticalStrut(Theme.PAD));
        addRow(card, nameRow);
        addRow(card, new LabelledField("Username", usernameField));
        addRow(card, new LabelledField("Password", passwordField));
        addRow(card, errorLabel);
        addRow(card, signupButton);
        addRow(card, backButton);

        card.setPreferredSize(new Dimension(
                CARD_WIDTH, card.getPreferredSize().height));
        add(card);

        signupButton.addActionListener(e -> signUp());
        passwordField.addActionListener(e -> signUp());
        backButton.addActionListener(e ->
                viewManagerModel.setActiveView(ViewNames.LOGIN));
    }

    /**
     * Sets the controller this view calls.
     *
     * @param signUpController the sign-up controller
     */
    public void setSignUpController(SignUpController signUpController) {
        this.signUpController = signUpController;
    }

    /**
     * Reacts to sign-up success or failure.
     *
     * @param evt the change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (viewModel.isSuccessful()) {
            errorLabel.setText(" ");
            passwordField.setText("");
            if (!GraphicsEnvironment.isHeadless()) {
                JOptionPane.showMessageDialog(this,
                        "Account created! Please log in.");
            }
            viewManagerModel.setActiveView(ViewNames.LOGIN);
        } else {
            errorLabel.setText(viewModel.getErrorMessage());
        }
    }

    private void signUp() {
        if (signUpController != null) {
            signUpController.execute(usernameField.getText(),
                    new String(passwordField.getPassword()),
                    firstNameField.getText(),
                    lastNameField.getText());
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
