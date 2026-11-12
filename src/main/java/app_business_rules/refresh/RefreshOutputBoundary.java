package app_business_rules.refresh;

/**
 * Defines the presenter contract for refresh.
 */
public interface RefreshOutputBoundary {

    /**
     * Presents successfully refreshed user data.
     *
     * @param outputData the refreshed data
     */
    void prepareSuccessView(RefreshOutputData outputData);

    /**
     * Presents a refresh failure.
     *
     * @param errorMessage the failure message
     */
    void prepareFailView(String errorMessage);
}
