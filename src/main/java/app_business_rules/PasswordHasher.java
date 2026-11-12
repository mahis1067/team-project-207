
package app_business_rules;

/**
 * Defines password hashing and verification operations.
 */
public interface PasswordHasher {

    /**
     * Hashes a plain-text password for secure storage.
     *
     * @param password the plain-text password
     * @return the hashed password
     */
    String hash(String password);

    /**
     * Verifies a plain-text password against a stored hash.
     *
     * @param password the plain-text password to verify
     * @param passwordHash the stored password hash
     * @return true if the password matches, false otherwise
     */
    boolean verify(String password, String passwordHash);
}
