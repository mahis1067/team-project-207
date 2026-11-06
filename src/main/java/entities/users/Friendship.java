package entities.users;

import java.time.LocalDateTime;

/** Represents a friendship or friend request between two users. */
public class Friendship {

  private User requestedBy;
  private User sentTo;
  private FriendshipStatus status;
  private LocalDateTime addedAt;

  /**
   * Creates a friendship or friend request between two users.
   *
   * @param requestedBy the user who initiated the request
   * @param sentTo the user who received the request
   * @param status the current friendship status
   * @param addedAt the date and time associated with the friendship
   */
  public Friendship(User requestedBy, User sentTo, FriendshipStatus status, LocalDateTime addedAt) {
    this.requestedBy = requestedBy;
    this.sentTo = sentTo;
    this.status = status;
    this.addedAt = addedAt;
  }

  /**
   * Returns the user who initiated the friend request.
   *
   * @return the requesting user
   */
  public User getRequestedBy() {
    return requestedBy;
  }

  /**
   * Returns the user who received the friend request.
   *
   * @return the receiving user
   */
  public User getSentTo() {
    return sentTo;
  }

  /**
   * Returns the current friendship status.
   *
   * @return the friendship status
   */
  public FriendshipStatus getStatus() {
    return status;
  }

  /**
   * Returns the date and time associated with the friendship.
   *
   * @return the friendship timestamp
   */
  public LocalDateTime getAddedAt() {
    return addedAt;
  }
}
