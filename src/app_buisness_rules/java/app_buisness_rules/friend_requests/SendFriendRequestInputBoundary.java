package app_buisness_rules.friend_requests;

/** Input boundary for sending a friend request. */
public interface SendFriendRequestInputBoundary {
  /**
   * Executes the send-friend-request use case.
   *
   * @param input the send-request input
   */
  void execute(SendFriendRequestInputData input);
}
