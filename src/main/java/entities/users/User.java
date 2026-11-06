package entities.users;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Represents a user of the expense tracking application. */
public class User {

  private UUID userId;
  private String username;
  private String passwordHash;
  private String firstName;
  private String lastName;
  private List<UUID> friendships;

  /**
   * Creates a user with the given profile and friendship information.
   *
   * @param userId the unique identifier of the user
   * @param username the username
   * @param passwordHash the hashed password
   * @param firstName the user's first name
   * @param lastName the user's last name
   * @param friendships the IDs of the user's friends
   */
  public User(
      UUID userId,
      String username,
      String passwordHash,
      String firstName,
      String lastName,
      List<UUID> friendships) {
    this.userId = userId;
    this.username = username;
    this.passwordHash = passwordHash;
    this.firstName = firstName;
    this.lastName = lastName;
    this.friendships = new ArrayList<>(friendships);
  }

  /**
   * Returns the user's unique identifier.
   *
   * @return the user ID
   */
  public UUID getUserId() {
    return userId;
  }

  /**
   * Returns the username.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Returns the hashed password.
   *
   * @return the password hash
   */
  public String getPasswordHash() {
    return passwordHash;
  }

  /**
   * Returns the user's first name.
   *
   * @return the first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Returns the user's last name.
   *
   * @return the last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Returns a copy of the user's friend IDs.
   *
   * @return a copy of the friend ID list
   */
  public List<UUID> getFriendships() {
    return new ArrayList<>(friendships);
  }
}
