package interface_adapters.friend_requests;

import java.util.UUID;

/** View model state for denying a friend request. */
public class DenyFriendRequestViewModel {
  private UUID requesterId;
  private String errorMessage;

  /** Creates an empty view model. */
  public DenyFriendRequestViewModel() {
  }

  /** @return the denied requester id */
  public UUID getRequesterId() {
    return requesterId;
  }

  /** @param requesterId the denied requester id */
  public void setRequesterId(UUID requesterId) {
    this.requesterId = requesterId;
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
