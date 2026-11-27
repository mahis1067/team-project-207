package app_buisness_rules.friend_requests;

import static org.junit.jupiter.api.Assertions.*;

import app_buisness_rules.notifications.NotificationEventGateway;
import entities.expenses.Expense;
import entities.users.Friendship;
import entities.users.User;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Tests notification publishing during friend request creation. */
class SendFriendRequestNotificationTest {

  @Test
  void successfulRequestPublishesNotificationToReceiver() {
    FakeFriendshipDataAccess dataAccess = new FakeFriendshipDataAccess();
    FakeNotifications notifications = new FakeNotifications();
    RecordingPresenter presenter = new RecordingPresenter();
    UUID senderId = UUID.randomUUID();

    new SendFriendRequestInteractor(dataAccess, presenter, notifications)
        .execute(new SendFriendRequestInputData(senderId, "receiver"));

    assertNotNull(dataAccess.savedFriendship);
    assertEquals(1, notifications.sentCount);
    assertEquals(senderId, notifications.senderId);
    assertEquals(dataAccess.receiver.getUserId(), notifications.receiverId);
    assertNotNull(presenter.success);
    assertNull(presenter.failure);
  }

  @Test
  void failedFriendshipSaveDoesNotPublishNotification() {
    FakeFriendshipDataAccess dataAccess = new FakeFriendshipDataAccess();
    dataAccess.failOnSave = true;
    FakeNotifications notifications = new FakeNotifications();
    RecordingPresenter presenter = new RecordingPresenter();

    new SendFriendRequestInteractor(dataAccess, presenter, notifications)
        .execute(new SendFriendRequestInputData(UUID.randomUUID(), "receiver"));

    assertEquals(0, notifications.sentCount);
    assertNull(presenter.success);
    assertNotNull(presenter.failure);
  }

  @Test
  void notificationFailureDoesNotUndoSuccessfulFriendship() {
    FakeFriendshipDataAccess dataAccess = new FakeFriendshipDataAccess();
    FakeNotifications notifications = new FakeNotifications();
    notifications.failOnSend = true;
    RecordingPresenter presenter = new RecordingPresenter();

    new SendFriendRequestInteractor(dataAccess, presenter, notifications)
        .execute(new SendFriendRequestInputData(UUID.randomUUID(), "receiver"));

    assertNotNull(dataAccess.savedFriendship);
    assertEquals(1, notifications.sentCount);
    assertNotNull(presenter.success);
    assertNull(presenter.failure);
  }

  private static class FakeFriendshipDataAccess
      implements SendFriendRequestDataAccessInterface {

    private final User receiver = new User(
        UUID.randomUUID(), "receiver", "", "", "", new ArrayList<>());
    private Friendship savedFriendship;
    private boolean failOnSave;

    @Override
    public User getUserByUsername(String username) {
      return receiver;
    }

    @Override
    public Friendship getFriendship(UUID senderId, UUID receiverId) {
      return null;
    }

    @Override
    public void saveFriendship(Friendship friendship) {
      if (failOnSave) {
        throw new IllegalStateException("Friendship save failed.");
      }
      savedFriendship = friendship;
    }
  }

  private static class FakeNotifications implements NotificationEventGateway {
    private int sentCount;
    private UUID senderId;
    private UUID receiverId;
    private boolean failOnSend;

    @Override
    public void friendRequestSent(UUID senderId, UUID receiverId) {
      sentCount++;
      this.senderId = senderId;
      this.receiverId = receiverId;
      if (failOnSend) {
        throw new IllegalStateException("Notification unavailable.");
      }
    }

    @Override
    public void friendRequestDenied(UUID senderId, UUID receiverId) {
      fail("Unexpected friend request denial notification.");
    }

    @Override
    public void expenseAdded(Expense expense) {
      fail("Unexpected expense notification.");
    }
  }

  private static class RecordingPresenter
      implements SendFriendRequestOutputBoundary {
    private SendFriendRequestOutputData success;
    private String failure;

    @Override
    public void prepareSuccessView(SendFriendRequestOutputData data) {
      success = data;
    }

    @Override
    public void prepareFailView(String message) {
      failure = message;
    }
  }
}
