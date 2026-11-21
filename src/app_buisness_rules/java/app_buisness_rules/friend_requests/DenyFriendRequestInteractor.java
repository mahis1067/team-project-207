package app_buisness_rules.friend_requests;

import entities.users.Friendship;

/** Interactor for denying a friend request. */
public class DenyFriendRequestInteractor implements DenyFriendRequestInputBoundary {
  private final DenyFriendRequestDataAccessInterface dataAccess;
  private final DenyFriendRequestOutputBoundary presenter;

  /**
   * Creates the deny-friend-request interactor.
   *
   * @param dataAccess friendship persistence
   * @param presenter output presenter
   */
  public DenyFriendRequestInteractor(
      DenyFriendRequestDataAccessInterface dataAccess,
      DenyFriendRequestOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
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
      dataAccess.deleteFriendship(input.userId(), input.requesterId());
      presenter.prepareSuccessView(
          new DenyFriendRequestOutputData(input.requesterId()));
    } catch (RuntimeException exception) {
      presenter.prepareFailView(exception.getMessage());
    }
  }
}
