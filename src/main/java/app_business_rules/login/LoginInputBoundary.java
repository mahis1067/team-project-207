package app_business_rules.login;

/**
 * Defines the input boundary for the login use case.
 */
public interface LoginInputBoundary {

    /**
     * Executes the login use case.
     *
     * @param inputData the login information
     */
    void execute(LoginInputData inputData);
}
