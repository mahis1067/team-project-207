
package app_business_rules.refresh;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Contains the user IDs affected by a refresh operation.
 */
public class RefreshInputData {

    private final List<UUID> userIds;

    /**
     * Constructs the refresh input data.
     *
     * @param userIds the IDs of users whose data should be refreshed
     */
    public RefreshInputData(List<UUID> userIds) {
        this.userIds = new ArrayList<>(userIds);
    }

    /**
     * Returns the affected user IDs.
     *
     * @return a list of user IDs
     */
    public List<UUID> getUserIds() {
        return new ArrayList<>(userIds);
    }
}
