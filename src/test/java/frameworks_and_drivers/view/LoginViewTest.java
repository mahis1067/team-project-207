package frameworks_and_drivers.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import interface_adapters.login.LoginViewModel;
import interface_adapters.view_manager.ViewManagerModel;
import interface_adapters.view_manager.ViewNames;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LoginViewTest {

    @Test
    void successfulLoginSwitchesToDashboard() {
        LoginViewModel viewModel = new LoginViewModel();
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        viewManagerModel.setActiveView(ViewNames.LOGIN);
        new LoginView(viewModel, viewManagerModel);

        viewModel.setSuccess(UUID.randomUUID(), "jane_doe");

        assertEquals(ViewNames.DASHBOARD, viewManagerModel.getActiveView());
    }

    @Test
    void failedLoginStaysOnLogin() {
        LoginViewModel viewModel = new LoginViewModel();
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        viewManagerModel.setActiveView(ViewNames.LOGIN);
        new LoginView(viewModel, viewManagerModel);

        viewModel.setError("Invalid username or password.");

        assertEquals(ViewNames.LOGIN, viewManagerModel.getActiveView());
    }
}
