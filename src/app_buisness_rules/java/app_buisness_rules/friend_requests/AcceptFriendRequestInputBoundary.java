package app_buisness_rules.friend_requests;

/** Input boundary for accepting a friend request. */
public interface AcceptFriendRequestInputBoundary {
  /**
   * Executes the accept-friend-request use case.
   *
   * @param input the accept-request input
   */
  void execute(AcceptFriendRequestInputData input);
}
