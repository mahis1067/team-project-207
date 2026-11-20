package interface_adapters.login;

import app_business_rules.login.LoginOutputBoundary;
import app_business_rules.login.LoginOutputData;

/**
 * Presenter for the login use case.
 */
public class LoginPresenter implements LoginOutputBoundary {

    private final LoginViewModel viewModel;

    /**
     * Constructs the login presenter.
     *
     * @param viewModel the login view model
     */
    public LoginPresenter(LoginViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Updates the view model after successful login.
     *
     * @param outputData the successful login result
     */
    @Override
    public void prepareSuccessView(LoginOutputData outputData) {
        viewModel.setSuccess(
                outputData.getUserId(),
                outputData.getUsername());
    }

    /**
     * Updates the view model after failed login.
     *
     * @param errorMessage the login failure message
     */
    @Override
    public void prepareFailView(String errorMessage) {
        viewModel.setError(errorMessage);
    }
}
