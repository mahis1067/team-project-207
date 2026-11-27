package entities.users;

import java.util.List;
import java.util.UUID;

/** Creates user entities from persisted or application-provided user data. */
public final class UserFactory {

  private UserFactory() {
  }

  /**
   * Creates a user with the supplied profile and friendship information.
   *
   * @param userId the user's unique identifier
   * @param username the username
   * @param passwordHash the stored password hash
   * @param firstName the user's first name
   * @param lastName the user's last name
   * @param friendships the IDs of the user's friends
   * @return the constructed user
   * @throws IllegalArgumentException if the user ID or friendship list is null
   */
  public static User create(
      final UUID userId,
      final String username,
      final String passwordHash,
      final String firstName,
      final String lastName,
      final List<UUID> friendships) {
    if (userId == null || friendships == null) {
      throw new IllegalArgumentException(
          "User ID and friendships cannot be null.");
    }

    return new User(
        userId, username, passwordHash, firstName, lastName, friendships);
  }
}
