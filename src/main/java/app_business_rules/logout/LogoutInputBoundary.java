package app_business_rules.logout;

/**
 * Defines the input boundary for logout.
 */
public interface LogoutInputBoundary {

    /**
     * Executes logout.
     *
     * @param inputData the logout information
     */
    void execute(LogoutInputData inputData);
}
