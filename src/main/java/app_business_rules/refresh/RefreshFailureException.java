package app_business_rules.refresh;

/**
 * Indicates that requested data could not be refreshed.
 */
public class RefreshFailureException extends RuntimeException {

    public RefreshFailureException(String message) {
        super(message);
    }
}
