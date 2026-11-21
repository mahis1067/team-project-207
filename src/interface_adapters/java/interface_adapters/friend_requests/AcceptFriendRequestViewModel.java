package interface_adapters.friend_requests;

import java.util.UUID;

/** View model state for accepting a friend request. */
public class AcceptFriendRequestViewModel {
  private UUID friendshipId;
  private String status;
  private String errorMessage;

  /** Creates an empty view model. */
  public AcceptFriendRequestViewModel() {
  }

  /** @return the accepted friendship id */
  public UUID getFriendshipId() {
    return friendshipId;
  }

  /** @param friendshipId the accepted friendship id */
  public void setFriendshipId(UUID friendshipId) {
    this.friendshipId = friendshipId;
  }

  /** @return the friendship status */
  public String getStatus() {
    return status;
  }

  /** @param status the friendship status */
  public void setStatus(String status) {
    this.status = status;
  }

  /** @return the error message, or null on success */
  public String getErrorMessage() {
    return errorMessage;
  }

  /** @param errorMessage the failure message */
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
