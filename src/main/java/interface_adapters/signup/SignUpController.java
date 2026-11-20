
package interface_adapters.signup;

import app_business_rules.signup.SignUpInputBoundary;
import app_business_rules.signup.SignUpInputData;

/**
 * Controller for the sign-up use case.
 */
public class SignUpController {

    private final SignUpInputBoundary interactor;

    /**
     * Constructs the sign-up controller.
     *
     * @param interactor the sign-up input boundary
     */
    public SignUpController(SignUpInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the sign-up use case.
     *
     * @param username the requested username
     * @param password the user's password
     * @param firstName the user's first name
     * @param lastName the user's last name
     */
    public void execute(String username, String password,
                        String firstName, String lastName) {
        SignUpInputData inputData = new SignUpInputData(
                username, password, firstName, lastName);

        interactor.execute(inputData);
    }
}
