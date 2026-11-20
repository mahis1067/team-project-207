
package app_business_rules.signup;

/**
 * Defines the input boundary for the sign-up use case.
 */
public interface SignUpInputBoundary {

    /**
     * Executes the sign-up use case.
     *
     * @param inputData the information required to sign up
     */
    void execute(SignUpInputData inputData);
}
