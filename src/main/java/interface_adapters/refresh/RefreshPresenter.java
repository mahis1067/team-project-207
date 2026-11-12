package interface_adapters.refresh;

import app_business_rules.refresh.RefreshOutputBoundary;
import app_business_rules.refresh.RefreshOutputData;

/**
 * Presenter for the refresh use case.
 */
public class RefreshPresenter implements RefreshOutputBoundary {

    private final RefreshViewModel viewModel;

    public RefreshPresenter(RefreshViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(RefreshOutputData outputData) {
        viewModel.setSuccess(outputData);
    }

    @Override
    public void prepareFailView(String errorMessage) {
        viewModel.setError(errorMessage);
    }
}
