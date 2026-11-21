package interface_adapters.friend_requests;

import app_buisness_rules.friend_requests.SendFriendRequestInputBoundary;
import app_buisness_rules.friend_requests.SendFriendRequestInputData;
import java.util.UUID;

/** Controller for sending a friend request. */
public class SendFriendRequestController {
  private final SendFriendRequestInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor send-request interactor
   */
  public SendFriendRequestController(SendFriendRequestInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Executes the send-friend-request use case.
   *
   * @param senderId the requesting user id
   * @param receiverUsername the receiving user's username
   */
  public void execute(UUID senderId, String receiverUsername) {
    interactor.execute(new SendFriendRequestInputData(senderId, receiverUsername));
  }
}
