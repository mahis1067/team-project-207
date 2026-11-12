
package app_business_rules.refresh;

/**
 * Defines the input boundary for the shared refresh use case.
 */
public interface RefreshInputBoundary {

    /**
     * Executes the refresh use case for the affected users.
     *
     * @param inputData the user IDs requiring a refresh
     */
    void execute(RefreshInputData inputData);
}
