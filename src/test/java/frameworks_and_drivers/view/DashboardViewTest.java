package frameworks_and_drivers.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import interface_adapters.login.LoginViewModel;
import interface_adapters.logout.LogoutViewModel;
import interface_adapters.view_manager.ViewManagerModel;
import interface_adapters.view_manager.ViewNames;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DashboardViewTest {

    @Test
    void successfulLogoutReturnsToLogin() {
        LogoutViewModel logoutViewModel = new LogoutViewModel();
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        viewManagerModel.setActiveView(ViewNames.DASHBOARD);
        new DashboardView(new LoginViewModel(), logoutViewModel,
                viewManagerModel);

        logoutViewModel.setSuccess(UUID.randomUUID());

        assertEquals(ViewNames.LOGIN, viewManagerModel.getActiveView());
    }

    @Test
    void failedLogoutStaysOnDashboard() {
        LogoutViewModel logoutViewModel = new LogoutViewModel();
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        viewManagerModel.setActiveView(ViewNames.DASHBOARD);
        new DashboardView(new LoginViewModel(), logoutViewModel,
                viewManagerModel);

        logoutViewModel.setError("No matching active session.");

        assertEquals(ViewNames.DASHBOARD,
                viewManagerModel.getActiveView());
    }
}
