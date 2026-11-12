package app_business_rules.logout;

import java.util.UUID;

/**
 * Implements the logout use case.
 */
public class LogoutInteractor implements LogoutInputBoundary {

    private final SessionState sessionState;
    private final LogoutOutputBoundary presenter;

    /**
     * Constructs the logout interactor.
     *
     * @param sessionState the current session
     * @param presenter the logout output boundary
     */
    public LogoutInteractor(
            SessionState sessionState,
            LogoutOutputBoundary presenter) {
        this.sessionState = sessionState;
        this.presenter = presenter;
    }

    /**
     * Executes logout for the authenticated user.
     *
     * @param inputData the logout input
     */
    @Override
    public void execute(LogoutInputData inputData) {
        if (inputData == null || inputData.getUserId() == null) {
            presenter.prepareFailView("User ID is required.");
            return;
        }

        UUID currentUserId = sessionState.getCurrentUserId();

        if (currentUserId == null
                || !currentUserId.equals(inputData.getUserId())) {
            presenter.prepareFailView("No matching active session.");
            return;
        }

        sessionState.clear();

        presenter.prepareSuccessView(
                new LogoutOutputData(currentUserId));
    }
}
