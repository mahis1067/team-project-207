package app_business_rules.logout;

/**
 * Defines the output boundary for logout.
 */
public interface LogoutOutputBoundary {

    /**
     * Prepares a successful logout result.
     *
     * @param outputData the logout result
     */
    void prepareSuccessView(LogoutOutputData outputData);

    /**
     * Prepares a logout failure message.
     *
     * @param errorMessage the failure message
     */
    void prepareFailView(String errorMessage);
}
