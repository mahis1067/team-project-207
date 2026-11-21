package app_buisness_rules.friend_requests;

/** Output boundary for presenting a denied friend request. */
public interface DenyFriendRequestOutputBoundary {
  /**
   * Presents success.
   *
   * @param output the denied request output
   */
  void prepareSuccessView(DenyFriendRequestOutputData output);

  /**
   * Presents failure.
   *
   * @param message the failure message
   */
  void prepareFailView(String message);
}
