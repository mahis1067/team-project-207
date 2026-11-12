package app_business_rules.refresh;

import entities.users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Reloads user information from persistent storage.
 */
public class RefreshInteractor implements RefreshInputBoundary {

    private final RefreshDataAccessInterface dataAccess;
    private final RefreshOutputBoundary presenter;

    public RefreshInteractor(
            RefreshDataAccessInterface dataAccess,
            RefreshOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(RefreshInputData inputData) {
        if (inputData == null || inputData.getUserIds() == null
                || inputData.getUserIds().isEmpty()) {
            presenter.prepareFailView("At least one user ID is required.");
            throw new RefreshFailureException(
                    "At least one user ID is required.");
        }

        List<RefreshOutputData.UserSnapshot> snapshots =
                new ArrayList<>();

        for (UUID userId : inputData.getUserIds()) {
            if (userId == null) {
                presenter.prepareFailView("User ID cannot be null.");
                throw new RefreshFailureException("User ID cannot be null.");
            }

            User user = dataAccess.getUserById(userId);

            if (user == null) {
                presenter.prepareFailView("User not found.");
                throw new RefreshFailureException("User not found.");
            }

            snapshots.add(new RefreshOutputData.UserSnapshot(
                    user.getUserId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName()));
        }

        presenter.prepareSuccessView(new RefreshOutputData(snapshots));
    }
}
