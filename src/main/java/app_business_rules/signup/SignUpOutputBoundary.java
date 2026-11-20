
package app_business_rules.signup;

/**
 * Defines the output boundary for the sign-up use case.
 */
public interface SignUpOutputBoundary {

    /**
     * Prepares the view when sign-up succeeds.
     *
     * @param outputData the successful sign-up result
     */
    void prepareSuccessView(SignUpOutputData outputData);

    /**
     * Prepares the view when sign-up fails.
     *
     * @param errorMessage the reason for the failure
     */
    void prepareFailView(String errorMessage);
}
