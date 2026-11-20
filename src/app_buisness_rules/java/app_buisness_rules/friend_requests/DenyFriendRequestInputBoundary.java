package app_buisness_rules.friend_requests;

/** Input boundary for denying a friend request. */
public interface DenyFriendRequestInputBoundary {
  /**
   * Executes the deny-friend-request use case.
   *
   * @param input the deny-request input
   */
  void execute(DenyFriendRequestInputData input);
}
