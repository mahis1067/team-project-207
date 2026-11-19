package app_business_rules.refresh;

/**
 * Indicates that requested data could not be refreshed.
 */
public class RefreshFailureException extends RuntimeException {

    /**
     * Creates a refresh failure with an error message.
     *
     * @param message the failure message
     */
    public RefreshFailureException(String message) {
        super(message);
    }

    /**
     * Creates a refresh failure with its original cause.
     *
     * @param message the failure message
     * @param cause the underlying exception
     */
    public RefreshFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
