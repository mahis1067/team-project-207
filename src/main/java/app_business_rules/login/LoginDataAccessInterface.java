package app_business_rules.login;

import entities.users.User;

/**
 * Defines the data access operations required for login.
 */
public interface LoginDataAccessInterface {

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return the matching user, or null if not found
     */
    User getUserByUsername(String username);
}
