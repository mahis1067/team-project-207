package app_business_rules.refresh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Contains refreshed, non-sensitive user information.
 */
public class RefreshOutputData {

    private final List<UserSnapshot> users;

    public RefreshOutputData(List<UserSnapshot> users) {
        this.users = Collections.unmodifiableList(
                new ArrayList<>(users));
    }

    public List<UserSnapshot> getUsers() {
        return users;
    }

    /**
     * A snapshot excluding authentication credentials.
     */
    public static class UserSnapshot {
        private final UUID userId;
        private final String username;
        private final String firstName;
        private final String lastName;

        public UserSnapshot(
                UUID userId,
                String username,
                String firstName,
                String lastName) {
            this.userId = userId;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public UUID getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }
}
