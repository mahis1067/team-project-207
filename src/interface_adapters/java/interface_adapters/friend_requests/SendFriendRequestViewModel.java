package interface_adapters.friend_requests;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;

/** View model state for sending a friend request. */
public class SendFriendRequestViewModel {
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);
  private SendFriendRequestState state = new SendFriendRequestState();

  /** @return the current state */
  public SendFriendRequestState getState() {
    return state;
  }

  /**
   * Adds a property change listener.
   *
   * @param listener listener to add
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  /** @return the requester's id */
  public UUID getRequesterId() {
    return state.requesterId();
  }

  /** @param requesterId the requester id */
  public void setRequesterId(UUID requesterId) {
    SendFriendRequestState oldState = state;
    state = new SendFriendRequestState(
        requesterId, state.receiverId(), state.status(), state.errorMessage());
    support.firePropertyChange("state", oldState, state);
  }

  /** @return the receiver id */
  public UUID getReceiverId() {
    return state.receiverId();
  }

  /** @param receiverId the receiver id */
  public void setReceiverId(UUID receiverId) {
    SendFriendRequestState oldState = state;
    state = new SendFriendRequestState(
        state.requesterId(), receiverId, state.status(), state.errorMessage());
    support.firePropertyChange("state", oldState, state);
  }

  /** @return the request status */
  public String getStatus() {
    return state.status();
  }

  /** @param status the request status */
  public void setStatus(String status) {
    SendFriendRequestState oldState = state;
    state = new SendFriendRequestState(
        state.requesterId(), state.receiverId(), status, state.errorMessage());
    support.firePropertyChange("state", oldState, state);
  }

  /** @return the error message */
  public String getErrorMessage() {
    return state.errorMessage();
  }

  /** @param errorMessage the error message */
  public void setErrorMessage(String errorMessage) {
    SendFriendRequestState oldState = state;
    state = new SendFriendRequestState(
        state.requesterId(), state.receiverId(), state.status(), errorMessage);
    support.firePropertyChange("state", oldState, state);
  }

  /** State for the send-friend-request view model. */
  public record SendFriendRequestState(
      UUID requesterId, UUID receiverId, String status, String errorMessage) {
    /** Creates an empty state. */
    public SendFriendRequestState() {
      this(null, null, null, null);
    }
  }
}
