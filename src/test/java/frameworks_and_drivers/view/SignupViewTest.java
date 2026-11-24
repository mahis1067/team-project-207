package frameworks_and_drivers.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import interface_adapters.signup.SignUpViewModel;
import interface_adapters.view_manager.ViewManagerModel;
import interface_adapters.view_manager.ViewNames;
import java.awt.Component;
import java.awt.Container;
import java.util.UUID;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class SignupViewTest {

    @Test
    void successfulSignupReturnsToLogin() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            SignUpViewModel viewModel = new SignUpViewModel();
            ViewManagerModel navigation = new ViewManagerModel();
            navigation.setActiveView(ViewNames.SIGNUP);
            new SignupView(viewModel, navigation);

            viewModel.setSuccess(UUID.randomUUID(), "test_user");

            assertEquals(ViewNames.LOGIN, navigation.getActiveView());
        });
    }

    @Test
    void failedSignupStaysOnSignup() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            SignUpViewModel viewModel = new SignUpViewModel();
            ViewManagerModel navigation = new ViewManagerModel();
            navigation.setActiveView(ViewNames.SIGNUP);
            new SignupView(viewModel, navigation);

            viewModel.setError("Username already exists.");

            assertEquals(ViewNames.SIGNUP, navigation.getActiveView());
        });
    }

    @Test
    void backButtonReturnsToLogin() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            SignUpViewModel viewModel = new SignUpViewModel();
            ViewManagerModel navigation = new ViewManagerModel();
            navigation.setActiveView(ViewNames.SIGNUP);

            SignupView view = new SignupView(viewModel, navigation);
            JButton backButton = findButton(view, "Back to log in");

            if (backButton == null) {
                throw new AssertionError("Back button not found");
            }

            backButton.doClick();

            assertEquals(ViewNames.LOGIN, navigation.getActiveView());
        });
    }


    @Test
    void signupButtonPassesCorrectInput() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            SignUpViewModel model = new SignUpViewModel();
            ViewManagerModel navigation = new ViewManagerModel();
            SignupView view = new SignupView(model, navigation);

            findField(view, "First name").setText("Jane");
            findField(view, "Last name").setText("Doe");
            findField(view, "Username").setText("jane_doe");
            findField(view, "Password").setText("test_password");

            app_business_rules.signup.SignUpInputData[] received =
                    new app_business_rules.signup.SignUpInputData[1];

            view.setSignUpController(
                    new interface_adapters.signup.SignUpController(
                            data -> received[0] = data));

            JButton button = findButton(view, "Sign up");
            org.junit.jupiter.api.Assertions.assertNotNull(button);
            button.doClick();

            org.junit.jupiter.api.Assertions.assertNotNull(received[0]);
            assertEquals("jane_doe", received[0].getUsername());
            assertEquals("test_password", received[0].getPassword());
            assertEquals("Jane", received[0].getFirstName());
            assertEquals("Doe", received[0].getLastName());
        });
    }

    @Test
    void successfulSignupClearsPassword() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            SignUpViewModel model = new SignUpViewModel();
            ViewManagerModel navigation = new ViewManagerModel();
            navigation.setActiveView(ViewNames.SIGNUP);
            SignupView view = new SignupView(model, navigation);

            javax.swing.JPasswordField password =
                    (javax.swing.JPasswordField) findField(view, "Password");

            password.setText("test_password");
            model.setSuccess(UUID.randomUUID(), "jane_doe");

            assertEquals(0, password.getPassword().length);
            assertEquals(ViewNames.LOGIN, navigation.getActiveView());
        });
    }

    @Test
    void failedSignupDisplaysError() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            SignUpViewModel model = new SignUpViewModel();
            ViewManagerModel navigation = new ViewManagerModel();
            navigation.setActiveView(ViewNames.SIGNUP);
            SignupView view = new SignupView(model, navigation);

            model.setError("Username already exists.");

            assertEquals(ViewNames.SIGNUP, navigation.getActiveView());
            org.junit.jupiter.api.Assertions.assertTrue(
                    containsLabel(view, "Username already exists."));
        });
    }

    private static javax.swing.JTextField findField(
            Component component, String fieldName) {
        if (component instanceof LabelledField labelledField) {
            java.awt.BorderLayout layout =
                    (java.awt.BorderLayout) labelledField.getLayout();
            Component label = layout.getLayoutComponent(
                    java.awt.BorderLayout.NORTH);
            Component input = layout.getLayoutComponent(
                    java.awt.BorderLayout.CENTER);

            if (label instanceof javax.swing.JLabel textLabel
                    && fieldName.equals(textLabel.getText())
                    && input instanceof javax.swing.JTextField textField) {
                return textField;
            }
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                try {
                    return findField(child, fieldName);
                } catch (IllegalArgumentException ignored) {
                    // Continue searching the other components.
                }
            }
        }

        throw new IllegalArgumentException(
                "Input field not found: " + fieldName);
    }

    private static boolean containsLabel(Component component, String text) {
        if (component instanceof javax.swing.JLabel label
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

    private static JButton findButton(Component component, String text) {
        if (component instanceof JButton button
                && text.equals(button.getText())) {
            return button;
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                JButton found = findButton(child, text);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
