
package interface_adapters.signup;

import app_business_rules.signup.SignUpOutputBoundary;
import app_business_rules.signup.SignUpOutputData;

/**
 * Presenter for the sign-up use case.
 */
public class SignUpPresenter implements SignUpOutputBoundary {

    private final SignUpViewModel viewModel;

    /**
     * Constructs the sign-up presenter.
     *
     * @param viewModel the sign-up view model
     */
    public SignUpPresenter(SignUpViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Updates the view model after successful sign-up.
     *
     * @param outputData the successful sign-up result
     */
    @Override
    public void prepareSuccessView(SignUpOutputData outputData) {
        viewModel.setSuccess(
                outputData.getUserId(),
                outputData.getUsername());
    }

    /**
     * Updates the view model after failed sign-up.
     *
     * @param errorMessage the sign-up failure message
     */
    @Override
    public void prepareFailView(String errorMessage) {
        viewModel.setError(errorMessage);
    }
}
