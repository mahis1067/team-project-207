package app_buisness_rules.friend_requests;

import entities.users.Friendship;
import entities.users.User;
import java.util.UUID;

/** Persistence boundary for sending friend requests. */
public interface SendFriendRequestDataAccessInterface {
  /**
   * Finds a user by username.
   *
   * @param username the username to find
   * @return the user, or null if unknown
   */
  User getUserByUsername(String username);

  /**
   * Finds a friendship for the two users.
   *
   * @param senderId the sender id
   * @param receiverId the receiver id
   * @return the friendship, or null if none exists
   */
  Friendship getFriendship(UUID senderId, UUID receiverId);

  /**
   * Saves a sent friend request.
   *
   * @param friendship the friendship to save
   */
  void saveFriendship(Friendship friendship);
}
