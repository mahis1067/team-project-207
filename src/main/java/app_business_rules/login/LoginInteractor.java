package app_business_rules.login;

import app_business_rules.PasswordHasher;
import app_business_rules.logout.SessionState;
import app_business_rules.refresh.RefreshInputBoundary;
import app_business_rules.refresh.RefreshInputData;
import entities.users.User;

import java.util.Collections;

/**
 * Implements the business logic for the login use case.
 */
public class LoginInteractor implements LoginInputBoundary {

    private final LoginDataAccessInterface dataAccess;
    private final LoginOutputBoundary presenter;
    private final PasswordHasher passwordHasher;
    private final RefreshInputBoundary refresh;
    private final SessionState sessionState;

    /**
     * Constructs the login interactor.
     *
     * @param dataAccess the user data access interface
     * @param presenter the login output boundary
     * @param passwordHasher the password verification service
     * @param refresh the shared refresh use case
     */
    public LoginInteractor(
            LoginDataAccessInterface dataAccess,
            LoginOutputBoundary presenter,
            PasswordHasher passwordHasher,
            RefreshInputBoundary refresh,
            SessionState sessionState) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.passwordHasher = passwordHasher;
        this.refresh = refresh;
        this.sessionState = sessionState;
    }

    /**
     * Executes the login use case.
     *
     * @param inputData the submitted login information
     */
    @Override
    public void execute(LoginInputData inputData) {
        if (inputData == null
                || isBlank(inputData.getUsername())
                || isBlank(inputData.getPassword())) {
            presenter.prepareFailView("Username and password are required.");
            return;
        }

        User user = dataAccess.getUserByUsername(inputData.getUsername());

        if (user == null
                || !passwordHasher.verify(
                        inputData.getPassword(), user.getPasswordHash())) {
            presenter.prepareFailView("Invalid username or password.");
            return;
        }

        refresh.execute(new RefreshInputData(
                Collections.singletonList(user.getUserId())));

        sessionState.setCurrentUserId(user.getUserId());

        LoginOutputData outputData =
                new LoginOutputData(user.getUserId(), user.getUsername());
        presenter.prepareSuccessView(outputData);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
