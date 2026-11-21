package interface_adapters.friend_requests;

import app_buisness_rules.friend_requests.AcceptFriendRequestInputBoundary;
import app_buisness_rules.friend_requests.AcceptFriendRequestInputData;
import java.util.UUID;

/** Controller for accepting a friend request. */
public class AcceptFriendRequestController {
  private final AcceptFriendRequestInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor accept-request interactor
   */
  public AcceptFriendRequestController(AcceptFriendRequestInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Executes the accept-friend-request use case.
   *
   * @param friendshipId the friendship to accept
   */
  public void execute(UUID friendshipId) {
    interactor.execute(new AcceptFriendRequestInputData(friendshipId));
  }
}
