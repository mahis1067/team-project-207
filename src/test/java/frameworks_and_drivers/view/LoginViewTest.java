package frameworks_and_drivers.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app_business_rules.login.LoginInputData;
import interface_adapters.login.LoginController;
import interface_adapters.login.LoginViewModel;
import interface_adapters.view_manager.ViewManagerModel;
import interface_adapters.view_manager.ViewNames;
import java.awt.Component;
import java.awt.Container;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class LoginViewTest {

    @Test
    void successfulLoginSwitchesToDashboard() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LoginViewModel viewModel = new LoginViewModel();
            ViewManagerModel manager = new ViewManagerModel();
            manager.setActiveView(ViewNames.LOGIN);
            new LoginView(viewModel, manager);

            viewModel.setSuccess(UUID.randomUUID(), "jane_doe");

            assertEquals(ViewNames.DASHBOARD, manager.getActiveView());
        });
    }

    @Test
    void failedLoginStaysOnLogin() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LoginViewModel viewModel = new LoginViewModel();
            ViewManagerModel manager = new ViewManagerModel();
            manager.setActiveView(ViewNames.LOGIN);
            new LoginView(viewModel, manager);

            viewModel.setError("Invalid username or password.");

            assertEquals(ViewNames.LOGIN, manager.getActiveView());
        });
    }

    @Test
    void loginButtonPassesCorrectInput() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LoginViewModel viewModel = new LoginViewModel();
            ViewManagerModel manager = new ViewManagerModel();
            LoginView view = new LoginView(viewModel, manager);
            AtomicReference<LoginInputData> received = new AtomicReference<>();
            view.setLoginController(new LoginController(received::set));

            findField(view, "Username").setText("jane_doe");
            JPasswordField password = (JPasswordField) findField(
                    view, "Password");
            password.setText("secret123");

            JButton button = findButton(view, "Log in");
            assertNotNull(button);
            button.doClick();

            assertNotNull(received.get());
            assertEquals("jane_doe", received.get().getUsername());
            assertEquals("secret123", received.get().getPassword());
        });
    }

    @Test
    void enterInPasswordFieldTriggersLogin() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LoginViewModel viewModel = new LoginViewModel();
            ViewManagerModel manager = new ViewManagerModel();
            LoginView view = new LoginView(viewModel, manager);
            AtomicReference<LoginInputData> received = new AtomicReference<>();
            view.setLoginController(new LoginController(received::set));

            findField(view, "Username").setText("enter_user");
            JPasswordField password = (JPasswordField) findField(
                    view, "Password");
            password.setText("enter_password");

            password.postActionEvent();

            assertNotNull(received.get());
            assertEquals("enter_user", received.get().getUsername());
            assertEquals("enter_password", received.get().getPassword());
        });
    }

    @Test
    void signupButtonSwitchesToSignup() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LoginViewModel viewModel = new LoginViewModel();
            ViewManagerModel manager = new ViewManagerModel();
            manager.setActiveView(ViewNames.LOGIN);
            LoginView view = new LoginView(viewModel, manager);

            JButton signup = findButton(view, "Create an account");
            assertNotNull(signup);
            signup.doClick();

            assertEquals(ViewNames.SIGNUP, manager.getActiveView());
        });
    }

    @Test
    void successfulLoginClearsPassword() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LoginViewModel viewModel = new LoginViewModel();
            ViewManagerModel manager = new ViewManagerModel();
            LoginView view = new LoginView(viewModel, manager);
            JPasswordField password = (JPasswordField) findField(
                    view, "Password");
            password.setText("secret123");

            viewModel.setSuccess(UUID.randomUUID(), "jane_doe");

            assertEquals(0, password.getPassword().length);
        });
    }

    @Test
    void failedLoginDisplaysError() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LoginViewModel viewModel = new LoginViewModel();
            ViewManagerModel manager = new ViewManagerModel();
            LoginView view = new LoginView(viewModel, manager);
            String message = "Invalid username or password.";

            viewModel.setError(message);

            assertTrue(containsLabel(view, message));
        });
    }

    @Test
    void successfulLoginClearsError() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LoginViewModel viewModel = new LoginViewModel();
            ViewManagerModel manager = new ViewManagerModel();
            LoginView view = new LoginView(viewModel, manager);
            String message = "Invalid username or password.";

            viewModel.setError(message);
            JLabel errorLabel = findLabel(view, message);
            assertNotNull(errorLabel);

            viewModel.setSuccess(UUID.randomUUID(), "jane_doe");

            assertEquals(" ", errorLabel.getText());
        });
    }

    private static JTextField findField(
            final Component component, final String fieldName) {
        if (component instanceof LabelledField labelledField) {
            java.awt.BorderLayout layout =
                    (java.awt.BorderLayout) labelledField.getLayout();
            Component label = layout.getLayoutComponent(
                    java.awt.BorderLayout.WEST);
            if (label instanceof JLabel text
                    && fieldName.equals(text.getText())) {
                for (Component child : labelledField.getComponents()) {
                    if (child instanceof JTextField field) {
                        return field;
                    }
                }
            }
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                JTextField result = findFieldOrNull(child, fieldName);
                if (result != null) {
                    return result;
                }
            }
        }
        throw new AssertionError("Input field not found: " + fieldName);
    }

    private static JTextField findFieldOrNull(
            final Component component, final String fieldName) {
        if (component instanceof LabelledField labelledField) {
            for (Component child : labelledField.getComponents()) {
                if (child instanceof JLabel label
                        && fieldName.equals(label.getText())) {
                    for (Component field : labelledField.getComponents()) {
                        if (field instanceof JTextField textField) {
                            return textField;
                        }
                    }
                }
            }
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                JTextField result = findFieldOrNull(child, fieldName);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static JLabel findLabel(
            final Component component, final String text) {
        if (component instanceof JLabel label
                && text.equals(label.getText())) {
            return label;
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                JLabel result = findLabel(child, text);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static boolean containsLabel(
            final Component component, final String text) {
        if (component instanceof JLabel label
                && text.equals(label.getText())) {
            return true;
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                if (containsLabel(child, text)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static JButton findButton(
            final Component component, final String text) {
        if (component instanceof JButton button
                && text.equals(button.getText())) {
            return button;
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                JButton result = findButton(child, text);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}
