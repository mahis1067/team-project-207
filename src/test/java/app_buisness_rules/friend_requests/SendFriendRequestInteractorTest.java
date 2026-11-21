package app_buisness_rules.friend_requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import entities.users.Friendship;
import entities.users.FriendshipStatus;
import entities.users.User;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Tests the send-friend-request interactor. */
class SendFriendRequestInteractorTest {
  /** Saves the request and presents success. */
  @Test
  void executeSavesRequestAndPresentsSuccess() {
    InMemoryDataAccess dataAccess = new InMemoryDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();
    SendFriendRequestInteractor interactor =
        new SendFriendRequestInteractor(dataAccess, presenter);
    UUID senderId = UUID.randomUUID();
    UUID receiverId = UUID.randomUUID();
    dataAccess.receiver = user(receiverId, "receiver");

    interactor.execute(
        new SendFriendRequestInputData(senderId, "receiver"));

    assertNotNull(dataAccess.savedFriendship);
    assertEquals(senderId, presenter.output.requesterId());
    assertEquals(receiverId, presenter.output.receiverId());
    assertEquals("SENT_REQUEST", presenter.output.status());
  }

  /** Rejects input when no sender id is supplied. */
  @Test
  void executeRejectsMissingSenderId() {
    InMemoryDataAccess dataAccess = new InMemoryDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();
    SendFriendRequestInteractor interactor =
        new SendFriendRequestInteractor(dataAccess, presenter);

    interactor.execute(
        new SendFriendRequestInputData(null, "receiver"));

    assertNull(dataAccess.savedFriendship);
    assertEquals("Sender id is required.", presenter.failureMessage);
  }

  /** Rejects input when no receiver username is supplied. */
  @Test
  void executeRejectsMissingReceiverUsername() {
    InMemoryDataAccess dataAccess = new InMemoryDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();
    SendFriendRequestInteractor interactor =
        new SendFriendRequestInteractor(dataAccess, presenter);

    interactor.execute(
        new SendFriendRequestInputData(UUID.randomUUID(), " "));

    assertNull(dataAccess.savedFriendship);
    assertEquals("Receiver username is required.", presenter.failureMessage);
  }

  /** Rejects an unknown receiver username. */
  @Test
  void executeRejectsUnknownReceiver() {
    InMemoryDataAccess dataAccess = new InMemoryDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();
    SendFriendRequestInteractor interactor =
        new SendFriendRequestInteractor(dataAccess, presenter);

    interactor.execute(
        new SendFriendRequestInputData(UUID.randomUUID(), "unknown"));

    assertNull(dataAccess.savedFriendship);
    assertEquals("Receiver does not exist.", presenter.failureMessage);
  }

  /** Rejects requests sent to the sender. */
  @Test
  void executeRejectsSelfRequest() {
    InMemoryDataAccess dataAccess = new InMemoryDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();
    SendFriendRequestInteractor interactor =
        new SendFriendRequestInteractor(dataAccess, presenter);
    UUID userId = UUID.randomUUID();
    dataAccess.receiver = user(userId, "same-user");

    interactor.execute(
        new SendFriendRequestInputData(userId, "same-user"));

    assertNull(dataAccess.savedFriendship);
    assertEquals(
        "Cannot send a friend request to yourself.",
        presenter.failureMessage);
  }

  /** Rejects a friendship or request that already exists. */
  @Test
  void executeRejectsExistingFriendship() {
    InMemoryDataAccess dataAccess = new InMemoryDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();
    SendFriendRequestInteractor interactor =
        new SendFriendRequestInteractor(dataAccess, presenter);
    UUID senderId = UUID.randomUUID();
    UUID receiverId = UUID.randomUUID();
    dataAccess.receiver = user(receiverId, "receiver");
    dataAccess.friendship = existingFriendship(senderId, receiverId);

    interactor.execute(
        new SendFriendRequestInputData(senderId, "receiver"));

    assertNull(dataAccess.savedFriendship);
    assertEquals(
        "A friendship or friend request already exists.",
        presenter.failureMessage);
  }

  /** Presents persistence failures to the output boundary. */
  @Test
  void executePresentsDataAccessFailure() {
    RecordingPresenter presenter = new RecordingPresenter();
    SendFriendRequestInteractor interactor =
        new SendFriendRequestInteractor(
            new SendFriendRequestDataAccessInterface() {
              @Override
              public User getUserByUsername(String username) {
                throw new IllegalStateException("Database unavailable.");
              }

              @Override
              public Friendship getFriendship(
                  UUID senderId, UUID receiverId) {
                return null;
              }

              @Override
              public void saveFriendship(Friendship friendship) {
              }
            },
            presenter);

    interactor.execute(
        new SendFriendRequestInputData(UUID.randomUUID(), "receiver"));

    assertEquals("Database unavailable.", presenter.failureMessage);
  }

  private static User user(UUID id, String username) {
    return new User(id, username, "", "", "", new ArrayList<>());
  }

  private static Friendship existingFriendship(
      UUID senderId, UUID receiverId) {
    return new Friendship(
        user(senderId, ""),
        user(receiverId, ""),
        FriendshipStatus.SENT_REQUEST,
        null);
  }

  private static class InMemoryDataAccess
      implements SendFriendRequestDataAccessInterface {
    private User receiver;
    private Friendship friendship;
    private Friendship savedFriendship;

    @Override
    public User getUserByUsername(String username) {
      return receiver;
    }

    @Override
    public Friendship getFriendship(UUID senderId, UUID receiverId) {
      return friendship;
    }

    @Override
    public void saveFriendship(Friendship friendship) {
      savedFriendship = friendship;
    }
  }

  private static class RecordingPresenter
      implements SendFriendRequestOutputBoundary {
    private SendFriendRequestOutputData output;
    private String failureMessage;

    @Override
    public void prepareSuccessView(SendFriendRequestOutputData data) {
      output = data;
    }

    @Override
    public void prepareFailView(String message) {
      failureMessage = message;
    }
  }
}
