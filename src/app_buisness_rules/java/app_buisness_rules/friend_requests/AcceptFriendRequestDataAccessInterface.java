package app_buisness_rules.friend_requests;

import java.util.UUID;

/** Persistence boundary for accepting friend requests. */
public interface AcceptFriendRequestDataAccessInterface {
  /**
   * Updates a friendship request to accepted.
   *
   * @param friendshipId the friendship to accept
   */
  void updateFriendship(UUID friendshipId);
}
