package app_buisness_rules.friend_requests;

import app_buisness_rules.notifications.NotificationEventGateway;
import entities.users.Friendship;

/** Interactor for denying a friend request. */
public class DenyFriendRequestInteractor implements DenyFriendRequestInputBoundary {
  private final DenyFriendRequestDataAccessInterface dataAccess;
  private final DenyFriendRequestOutputBoundary presenter;
  private final NotificationEventGateway notifications;

  /**
   * Creates the deny-friend-request interactor.
   *
   * @param dataAccess friendship persistence
   * @param presenter output presenter
   */
  public DenyFriendRequestInteractor(
      DenyFriendRequestDataAccessInterface dataAccess,
      DenyFriendRequestOutputBoundary presenter) {
    this(dataAccess, presenter, null);
  }

  /**
   * Creates an interactor with notification publishing.
   *
   * @param dataAccess friendship persistence
   * @param presenter output presenter
   * @param notifications notification event gateway
   */
  public DenyFriendRequestInteractor(
      DenyFriendRequestDataAccessInterface dataAccess,
      DenyFriendRequestOutputBoundary presenter,
      NotificationEventGateway notifications) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
    this.notifications = notifications;
  }

  @Override
  public void execute(DenyFriendRequestInputData input) {
    if (input.userId() == null) {
      presenter.prepareFailView("User id is required.");
      return;
    }
    if (input.requesterId() == null) {
      presenter.prepareFailView("Requester id is required.");
      return;
    }
    try {
      Friendship friendship =
          dataAccess.getFriendship(input.userId(), input.requesterId());
      if (friendship == null) {
        presenter.prepareFailView("Friend request not found.");
        return;
      }
      if (friendship.getStatus()
          != entities.users.FriendshipStatus.SENT_REQUEST) {
        presenter.prepareFailView("Friend request is not pending.");
        return;
      }

      if (!dataAccess.deleteFriendship(
          input.userId(), input.requesterId())) {
        presenter.prepareFailView(
            "Friend request was already handled.");
        return;
      }

      if (notifications != null) {
        try {
          notifications.friendRequestDenied(input.userId(), input.requesterId());
        } catch (RuntimeException notificationFailure) {
          // Friendship was deleted; notification delivery is best-effort.
          java.util.logging.Logger.getLogger(
              DenyFriendRequestInteractor.class.getName())
              .log(java.util.logging.Level.WARNING,
                  "Friend request denied, but notification publishing failed.",
                  notificationFailure);
        }
      }
      presenter.prepareSuccessView(
          new DenyFriendRequestOutputData(input.requesterId()));
    } catch (RuntimeException exception) {
      presenter.prepareFailView(exception.getMessage());
    }
  }
}
