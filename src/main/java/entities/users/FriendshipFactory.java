package entities.users;

import java.time.LocalDateTime;

/** Creates friendship entities from persisted or application-provided data. */
public final class FriendshipFactory {

  private FriendshipFactory() {
  }

  /**
   * Creates a friendship or friend request.
   *
   * @param requestedBy the user who initiated the request
   * @param sentTo the user who received the request
   * @param status the current friendship status
   * @param addedAt the date and time associated with the friendship
   * @return the constructed friendship
   */
  public static Friendship create(
      final User requestedBy,
      final User sentTo,
      final FriendshipStatus status,
      final LocalDateTime addedAt) {
    return new Friendship(requestedBy, sentTo, status, addedAt);
  }
}
