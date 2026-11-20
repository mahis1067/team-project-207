package interface_adapters.refresh;

import app_business_rules.refresh.RefreshOutputData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores the latest refreshed user information.
 */
public class RefreshViewModel {

    private List<RefreshOutputData.UserSnapshot> users =
            Collections.emptyList();

    private String errorMessage;
    private boolean successful;

    public void setSuccess(RefreshOutputData outputData) {
        users = Collections.unmodifiableList(
                new ArrayList<>(outputData.getUsers()));
        errorMessage = null;
        successful = true;
    }

    public void setError(String message) {
        errorMessage = message;
        successful = false;
    }

    public List<RefreshOutputData.UserSnapshot> getUsers() {
        return users;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
