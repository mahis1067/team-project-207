package app_buisness_rules.friend_requests;

/** Output boundary for presenting a sent friend request. */
public interface SendFriendRequestOutputBoundary {
  /**
   * Presents success.
   *
   * @param output the sent request output
   */
  void prepareSuccessView(SendFriendRequestOutputData output);

  /**
   * Presents failure.
   *
   * @param message the failure message
   */
  void prepareFailView(String message);
}
