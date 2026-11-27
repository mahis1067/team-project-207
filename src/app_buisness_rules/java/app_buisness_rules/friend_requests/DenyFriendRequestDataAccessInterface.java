package app_buisness_rules.friend_requests;

import entities.users.Friendship;
import java.util.UUID;

/** Persistence boundary for denying friend requests. */
public interface DenyFriendRequestDataAccessInterface {
  /**
   * Finds the pending friendship between the receiver and requester.
   *
   * @param userId the receiving user id
   * @param requesterId the requesting user id
   * @return the friendship, or null if it does not exist
   */
  Friendship getFriendship(UUID userId, UUID requesterId);

  /**
   * Deletes a friendship request.
   *
   * @param userId the receiving user id
   * @param requesterId the requesting user id
   */
  boolean deleteFriendship(UUID userId, UUID requesterId);
}
