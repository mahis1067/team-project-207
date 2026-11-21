package app_buisness_rules.friend_requests;

import entities.users.Friendship;
import entities.users.FriendshipStatus;
import entities.users.User;
import java.time.LocalDateTime;

/** Interactor for sending a friend request. */
public class SendFriendRequestInteractor implements SendFriendRequestInputBoundary {
  private final SendFriendRequestDataAccessInterface dataAccess;
  private final SendFriendRequestOutputBoundary presenter;

  /**
   * Creates the send-friend-request interactor.
   *
   * @param dataAccess friendship persistence
   * @param presenter output presenter
   */
  public SendFriendRequestInteractor(
      SendFriendRequestDataAccessInterface dataAccess,
      SendFriendRequestOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(SendFriendRequestInputData input) {
    if (input.senderId() == null) {
      presenter.prepareFailView("Sender id is required.");
      return;
    }
    if (input.receiverUsername() == null || input.receiverUsername().isBlank()) {
      presenter.prepareFailView("Receiver username is required.");
      return;
    }
    try {
      User receiver = dataAccess.getUserByUsername(input.receiverUsername());
      if (receiver == null) {
        presenter.prepareFailView("Receiver does not exist.");
        return;
      }
      if (input.senderId().equals(receiver.getUserId())) {
        presenter.prepareFailView("Cannot send a friend request to yourself.");
        return;
      }
      if (dataAccess.getFriendship(input.senderId(), receiver.getUserId()) != null) {
        presenter.prepareFailView("A friendship or friend request already exists.");
        return;
      }
      User sender = new User(
          input.senderId(), "", "", "", "", java.util.Collections.emptyList());
      Friendship friendship = new Friendship(
          sender,
          receiver,
          FriendshipStatus.SENT_REQUEST,
          LocalDateTime.now());
      dataAccess.saveFriendship(friendship);
      presenter.prepareSuccessView(
          new SendFriendRequestOutputData(
              input.senderId(),
              receiver.getUserId(),
              "SENT_REQUEST"));
    } catch (RuntimeException exception) {
      presenter.prepareFailView(exception.getMessage());
    }
  }
}
