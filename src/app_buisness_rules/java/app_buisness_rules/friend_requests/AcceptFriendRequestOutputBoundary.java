package app_buisness_rules.friend_requests;

/** Output boundary for presenting an accepted friend request. */
public interface AcceptFriendRequestOutputBoundary {
  /**
   * Presents success.
   *
   * @param output the accepted request output
   */
  void prepareSuccessView(AcceptFriendRequestOutputData output);

  /**
   * Presents failure.
   *
   * @param message the failure message
   */
  void prepareFailView(String message);
}
