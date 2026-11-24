package frameworks_and_drivers.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app_business_rules.logout.LogoutInputData;
import interface_adapters.login.LoginViewModel;
import interface_adapters.logout.LogoutController;
import interface_adapters.logout.LogoutViewModel;
import interface_adapters.view_manager.ViewManagerModel;
import interface_adapters.view_manager.ViewNames;
import java.awt.Component;
import java.awt.Container;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class DashboardViewTest {

    @Test
    void successfulLogoutReturnsToLogin() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LogoutViewModel logoutModel = new LogoutViewModel();
            ViewManagerModel manager = new ViewManagerModel();
            manager.setActiveView(ViewNames.DASHBOARD);
            new DashboardView(new LoginViewModel(), logoutModel, manager);

            logoutModel.setSuccess(UUID.randomUUID());

            assertEquals(ViewNames.LOGIN, manager.getActiveView());
        });
    }

    @Test
    void failedLogoutStaysOnDashboard() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LogoutViewModel logoutModel = new LogoutViewModel();
            ViewManagerModel manager = new ViewManagerModel();
            manager.setActiveView(ViewNames.DASHBOARD);
            new DashboardView(new LoginViewModel(), logoutModel, manager);

            logoutModel.setError("No matching active session.");

            assertEquals(ViewNames.DASHBOARD, manager.getActiveView());
        });
    }

    @Test
    void logoutButtonPassesCorrectUserId() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LoginViewModel loginModel = new LoginViewModel();
            LogoutViewModel logoutModel = new LogoutViewModel();
            ViewManagerModel manager = new ViewManagerModel();
            DashboardView view = new DashboardView(
                    loginModel, logoutModel, manager);

            AtomicReference<LogoutInputData> received =
                    new AtomicReference<>();
            view.setLogoutController(new LogoutController(received::set));

            UUID userId = UUID.randomUUID();
            loginModel.setSuccess(userId, "jane_doe");

            JButton button = findButton(view, "Log out");
            assertNotNull(button);
            button.doClick();

            assertNotNull(received.get());
            assertEquals(userId, received.get().getUserId());
        });
    }

    @Test
    void logoutWithoutUserDoesNotCallController() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LoginViewModel loginModel = new LoginViewModel();
            LogoutViewModel logoutModel = new LogoutViewModel();
            ViewManagerModel manager = new ViewManagerModel();
            DashboardView view = new DashboardView(
                    loginModel, logoutModel, manager);

            AtomicInteger calls = new AtomicInteger();
            view.setLogoutController(new LogoutController(
                    data -> calls.incrementAndGet()));

            JButton button = findButton(view, "Log out");
            assertNotNull(button);
            button.doClick();

            assertEquals(0, calls.get());
        });
    }

    @Test
    void logoutAfterSuccessDoesNotCallControllerAgain() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LoginViewModel loginModel = new LoginViewModel();
            LogoutViewModel logoutModel = new LogoutViewModel();
            ViewManagerModel manager = new ViewManagerModel();
            DashboardView view = new DashboardView(
                    loginModel, logoutModel, manager);

            AtomicInteger calls = new AtomicInteger();
            view.setLogoutController(new LogoutController(
                    data -> calls.incrementAndGet()));

            UUID userId = UUID.randomUUID();
            loginModel.setSuccess(userId, "jane_doe");

            JButton button = findButton(view, "Log out");
            assertNotNull(button);
            button.doClick();
            assertEquals(1, calls.get());

            logoutModel.setSuccess(userId);
            assertEquals(ViewNames.LOGIN, manager.getActiveView());

            button.doClick();
            assertEquals(1, calls.get());
        });
    }

    @Test
    void loginUpdatesWelcomeMessage() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LoginViewModel loginModel = new LoginViewModel();
            DashboardView view = new DashboardView(
                    loginModel,
                    new LogoutViewModel(),
                    new ViewManagerModel());

            loginModel.setSuccess(UUID.randomUUID(), "jane_doe");

            assertTrue(containsLabel(
                    view, "Welcome back, jane_doe!"));
        });
    }

    @Test
    void addExpenseButtonSwitchesToExpense() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ViewManagerModel manager = new ViewManagerModel();
            manager.setActiveView(ViewNames.DASHBOARD);
            DashboardView view = new DashboardView(
                    new LoginViewModel(),
                    new LogoutViewModel(),
                    manager);

            JButton button = findButton(view, "+ Add expense");
            assertNotNull(button);
            button.doClick();

            assertEquals(ViewNames.EXPENSE, manager.getActiveView());
        });
    }

    @Test
    void failedLogoutPreservesUserSession() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LoginViewModel loginModel = new LoginViewModel();
            LogoutViewModel logoutModel = new LogoutViewModel();
            ViewManagerModel manager = new ViewManagerModel();
            manager.setActiveView(ViewNames.DASHBOARD);
            DashboardView view = new DashboardView(
                    loginModel, logoutModel, manager);

            AtomicInteger calls = new AtomicInteger();
            AtomicReference<LogoutInputData> received =
                    new AtomicReference<>();
            view.setLogoutController(new LogoutController(data -> {
                calls.incrementAndGet();
                received.set(data);
            }));

            UUID userId = UUID.randomUUID();
            loginModel.setSuccess(userId, "jane_doe");

            JButton button = findButton(view, "Log out");
            assertNotNull(button);
            button.doClick();
            assertEquals(1, calls.get());

            logoutModel.setError("No matching active session.");
            assertEquals(ViewNames.DASHBOARD, manager.getActiveView());

            button.doClick();
            assertEquals(2, calls.get());
            assertNotNull(received.get());
            assertEquals(userId, received.get().getUserId());
        });
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
}
