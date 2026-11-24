package interface_adapters.friend_requests;

import app_buisness_rules.friend_requests.SendFriendRequestOutputBoundary;
import app_buisness_rules.friend_requests.SendFriendRequestOutputData;

/** Presenter for sent friend requests. */
public class SendFriendRequestPresenter implements SendFriendRequestOutputBoundary {
  private final SendFriendRequestViewModel viewModel;

  /** @param viewModel view model updated by this presenter */
  public SendFriendRequestPresenter(SendFriendRequestViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(SendFriendRequestOutputData output) {
    viewModel.setRequesterId(output.requesterId());
    viewModel.setReceiverId(output.receiverId());
    viewModel.setStatus(output.status());
    viewModel.setErrorMessage(null);
  }

  @Override
  public void prepareFailView(String message) {
    viewModel.setRequesterId(null);
    viewModel.setReceiverId(null);
    viewModel.setStatus(null);
    viewModel.setErrorMessage(message);
  }
}
