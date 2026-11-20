package interface_adapters.login;

import app_business_rules.login.LoginInputBoundary;
import app_business_rules.login.LoginInputData;

/**
 * Controller for the login use case.
 */
public class LoginController {

    private final LoginInputBoundary interactor;

    /**
     * Constructs the login controller.
     *
     * @param interactor the login input boundary
     */
    public LoginController(LoginInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the login use case.
     *
     * @param username the username
     * @param password the password
     */
    public void execute(String username, String password) {
        LoginInputData inputData = new LoginInputData(username, password);
        interactor.execute(inputData);
    }
}
