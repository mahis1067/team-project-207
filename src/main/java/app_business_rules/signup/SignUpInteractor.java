
package app_business_rules.signup;

import app_business_rules.PasswordHasher;
import app_business_rules.refresh.RefreshInputBoundary;
import app_business_rules.refresh.RefreshInputData;
import app_business_rules.refresh.RefreshFailureException;
import entities.users.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

/**
 * Implements the business logic for the sign-up use case.
 */
public class SignUpInteractor implements SignUpInputBoundary {

    private final SignUpDataAccessInterface dataAccess;
    private final SignUpOutputBoundary presenter;
    private final PasswordHasher passwordHasher;
    private final RefreshInputBoundary refresh;

    /**
     * Constructs the sign-up interactor.
     *
     * @param dataAccess the data access interface for user registration
     * @param presenter the output boundary for sign-up results
     * @param passwordHasher the password hashing service
     * @param refresh the shared refresh use case
     */
    public SignUpInteractor(
            SignUpDataAccessInterface dataAccess,
            SignUpOutputBoundary presenter,
            PasswordHasher passwordHasher,
            RefreshInputBoundary refresh) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.passwordHasher = passwordHasher;
        this.refresh = refresh;
    }

    /**
     * Executes the sign-up use case.
     *
     * @param inputData the sign-up information
     */
    @Override
    public void execute(SignUpInputData inputData) {
        if (inputData == null
                || isBlank(inputData.getUsername())
                || isBlank(inputData.getPassword())
                || isBlank(inputData.getFirstName())
                || isBlank(inputData.getLastName())) {
            presenter.prepareFailView("All fields are required.");
            return;
        }

        if (dataAccess.existsByUsername(inputData.getUsername())) {
            presenter.prepareFailView("Username already exists.");
            return;
        }

        String passwordHash = passwordHasher.hash(inputData.getPassword());

        UUID userId = UUID.randomUUID();

        User user = new User(
                userId,
                inputData.getUsername(),
                passwordHash,
                inputData.getFirstName(),
                inputData.getLastName(),
                new ArrayList<UUID>()
        );

        UUID savedUserId = dataAccess.saveUser(user);
        if (savedUserId == null) {
            throw new IllegalStateException(
                    "Data access returned a null user ID.");
        }

        try {
            refresh.execute(new RefreshInputData(
                    Collections.singletonList(savedUserId)));
        } catch (RefreshFailureException exception) {
            presenter.prepareFailView(
                    "Account created, but data refresh failed. "
                    + "Please sign in again.");
            return;
        }

        SignUpOutputData outputData =
                new SignUpOutputData(savedUserId, inputData.getUsername());
        presenter.prepareSuccessView(outputData);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
