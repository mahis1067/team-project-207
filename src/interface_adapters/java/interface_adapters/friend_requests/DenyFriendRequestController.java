package interface_adapters.friend_requests;

import app_buisness_rules.friend_requests.DenyFriendRequestInputBoundary;
import app_buisness_rules.friend_requests.DenyFriendRequestInputData;
import java.util.UUID;

/** Controller for denying a friend request. */
public class DenyFriendRequestController {
  private final DenyFriendRequestInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor deny-request interactor
   */
  public DenyFriendRequestController(DenyFriendRequestInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Executes the deny-friend-request use case.
   *
   * @param userId the receiving user id
   * @param requesterId the requesting user id
   */
  public void execute(UUID userId, UUID requesterId) {
    interactor.execute(new DenyFriendRequestInputData(userId, requesterId));
  }
}
