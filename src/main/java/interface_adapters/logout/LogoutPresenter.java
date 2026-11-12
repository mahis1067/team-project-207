package interface_adapters.logout;

import app_business_rules.logout.LogoutOutputBoundary;
import app_business_rules.logout.LogoutOutputData;

/**
 * Presenter for the logout use case.
 */
public class LogoutPresenter implements LogoutOutputBoundary {

    private final LogoutViewModel viewModel;

    public LogoutPresenter(LogoutViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(LogoutOutputData outputData) {
        viewModel.setSuccess(outputData.getUserId());
    }

    @Override
    public void prepareFailView(String errorMessage) {
        viewModel.setError(errorMessage);
    }
}
