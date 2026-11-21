package interface_adapters.friend_requests;

import app_buisness_rules.friend_requests.AcceptFriendRequestOutputBoundary;
import app_buisness_rules.friend_requests.AcceptFriendRequestOutputData;

/** Presenter for accepted friend requests. */
public class AcceptFriendRequestPresenter implements AcceptFriendRequestOutputBoundary {
  private final AcceptFriendRequestViewModel viewModel;

  /** @param viewModel view model updated by this presenter */
  public AcceptFriendRequestPresenter(AcceptFriendRequestViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(AcceptFriendRequestOutputData output) {
    viewModel.setFriendshipId(output.friendshipId());
    viewModel.setStatus(output.status());
    viewModel.setErrorMessage(null);
  }

  @Override
  public void prepareFailView(String message) {
    viewModel.setFriendshipId(null);
    viewModel.setStatus(null);
    viewModel.setErrorMessage(message);
  }
}
