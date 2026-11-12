
package app_business_rules.signup;

import entities.users.User;
import java.util.UUID;

/**
 * Defines the data access operations required by the sign-up use case.
 */
public interface SignUpDataAccessInterface {

    /**
     * Checks whether a username already exists.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Saves a newly registered user.
     *
     * @param user the user to save
     * @return the persisted user's ID
     */
    UUID saveUser(User user);
}
