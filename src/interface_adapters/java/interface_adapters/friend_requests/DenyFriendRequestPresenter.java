package interface_adapters.friend_requests;

import app_buisness_rules.friend_requests.DenyFriendRequestOutputBoundary;
import app_buisness_rules.friend_requests.DenyFriendRequestOutputData;

/** Presenter for denied friend requests. */
public class DenyFriendRequestPresenter implements DenyFriendRequestOutputBoundary {
  private final DenyFriendRequestViewModel viewModel;

  /** @param viewModel view model updated by this presenter */
  public DenyFriendRequestPresenter(DenyFriendRequestViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(DenyFriendRequestOutputData output) {
    viewModel.setRequesterId(output.requesterId());
    viewModel.setErrorMessage(null);
  }

  @Override
  public void prepareFailView(String message) {
    viewModel.setRequesterId(null);
    viewModel.setErrorMessage(message);
  }
}
