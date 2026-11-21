package app_buisness_rules.friend_requests;

/** Interactor for accepting a friend request. */
public class AcceptFriendRequestInteractor implements AcceptFriendRequestInputBoundary {
  private final AcceptFriendRequestDataAccessInterface dataAccess;
  private final AcceptFriendRequestOutputBoundary presenter;

  /**
   * Creates the accept-friend-request interactor.
   *
   * @param dataAccess friendship persistence
   * @param presenter output presenter
   */
  public AcceptFriendRequestInteractor(
      AcceptFriendRequestDataAccessInterface dataAccess,
      AcceptFriendRequestOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(AcceptFriendRequestInputData input) {
    if (input.friendshipId() == null) {
      presenter.prepareFailView("Friendship id is required.");
      return;
    }
    try {
      dataAccess.updateFriendship(input.friendshipId());
      presenter.prepareSuccessView(
          new AcceptFriendRequestOutputData(input.friendshipId(), "ADDED"));
    } catch (RuntimeException exception) {
      presenter.prepareFailView(exception.getMessage());
    }
  }
}
