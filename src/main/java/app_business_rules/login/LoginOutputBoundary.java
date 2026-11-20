package app_business_rules.login;

/**
 * Defines the output boundary for the login use case.
 */
public interface LoginOutputBoundary {

    /**
     * Prepares the successful login result.
     *
     * @param outputData the successful login data
     */
    void prepareSuccessView(LoginOutputData outputData);

    /**
     * Prepares a login failure message.
     *
     * @param errorMessage the failure message
     */
    void prepareFailView(String errorMessage);
}
