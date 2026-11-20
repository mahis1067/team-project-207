package interface_adapters.logout;

import app_business_rules.logout.LogoutInputBoundary;
import app_business_rules.logout.LogoutInputData;

import java.util.UUID;

/**
 * Controller for the logout use case.
 */
public class LogoutController {

    private final LogoutInputBoundary interactor;

    public LogoutController(LogoutInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(UUID userId) {
        interactor.execute(new LogoutInputData(userId));
    }
}
