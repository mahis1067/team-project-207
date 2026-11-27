package app_buisness_rules.friend_requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import app_buisness_rules.notifications.NotificationEventGateway;
import entities.expenses.Expense;
import entities.users.Friendship;
import entities.users.FriendshipStatus;
import entities.users.User;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Tests notification publishing when denying friend requests. */
class DenyFriendRequestNotificationTest {

  @Test
  void successfulDenialNotifiesOriginalRequester() {
    FakeFriendshipDataAccess dataAccess = new FakeFriendshipDataAccess();
    FakeNotifications notifications = new FakeNotifications();
    RecordingPresenter presenter = new RecordingPresenter();
    UUID userId = UUID.randomUUID();
    UUID requesterId = UUID.randomUUID();
    dataAccess.friendship = pendingFriendship(userId, requesterId);

    new DenyFriendRequestInteractor(dataAccess, presenter, notifications)
        .execute(new DenyFriendRequestInputData(userId, requesterId));

    assertEquals(userId, dataAccess.deletedUserId);
    assertEquals(requesterId, dataAccess.deletedRequesterId);
    assertEquals(1, notifications.deniedCount);
    assertEquals(userId, notifications.senderId);
    assertEquals(requesterId, notifications.receiverId);
    assertNotNull(presenter.success);
    assertNull(presenter.failure);
  }

  @Test
  void missingRequestDoesNotPublishNotification() {
    FakeFriendshipDataAccess dataAccess = new FakeFriendshipDataAccess();
    FakeNotifications notifications = new FakeNotifications();
    RecordingPresenter presenter = new RecordingPresenter();

    new DenyFriendRequestInteractor(dataAccess, presenter, notifications)
        .execute(new DenyFriendRequestInputData(
            UUID.randomUUID(), UUID.randomUUID()));

    assertEquals(0, notifications.deniedCount);
    assertNull(presenter.success);
    assertEquals("Friend request not found.", presenter.failure);
  }

  @Test
  void failedDeletionDoesNotPublishNotification() {
    FakeFriendshipDataAccess dataAccess = new FakeFriendshipDataAccess();
    FakeNotifications notifications = new FakeNotifications();
    RecordingPresenter presenter = new RecordingPresenter();
    UUID userId = UUID.randomUUID();
    UUID requesterId = UUID.randomUUID();
    dataAccess.friendship = pendingFriendship(userId, requesterId);
    dataAccess.failOnDelete = true;

    new DenyFriendRequestInteractor(dataAccess, presenter, notifications)
        .execute(new DenyFriendRequestInputData(userId, requesterId));

    assertEquals(0, notifications.deniedCount);
    assertNull(presenter.success);
    assertEquals("Friendship deletion failed.", presenter.failure);
  }

  @Test
  void notificationFailureDoesNotUndoSuccessfulDenial() {
    FakeFriendshipDataAccess dataAccess = new FakeFriendshipDataAccess();
    FakeNotifications notifications = new FakeNotifications();
    RecordingPresenter presenter = new RecordingPresenter();
    UUID userId = UUID.randomUUID();
    UUID requesterId = UUID.randomUUID();
    dataAccess.friendship = pendingFriendship(userId, requesterId);
    notifications.failOnDeny = true;

    new DenyFriendRequestInteractor(dataAccess, presenter, notifications)
        .execute(new DenyFriendRequestInputData(userId, requesterId));

    assertEquals(userId, dataAccess.deletedUserId);
    assertEquals(requesterId, dataAccess.deletedRequesterId);
    assertEquals(1, notifications.deniedCount);
    assertNotNull(presenter.success);
    assertNull(presenter.failure);
  }

  private static Friendship pendingFriendship(
      UUID userId, UUID requesterId) {
    User requester = new User(
        requesterId, "", "", "", "", new ArrayList<>());
    User receiver = new User(
        userId, "", "", "", "", new ArrayList<>());
    return new Friendship(
        requester, receiver, FriendshipStatus.SENT_REQUEST, null);
  }

  private static class FakeFriendshipDataAccess
      implements DenyFriendRequestDataAccessInterface {

    private Friendship friendship;
    private UUID deletedUserId;
    private UUID deletedRequesterId;
    private boolean failOnDelete;

    @Override
    public Friendship getFriendship(UUID userId, UUID requesterId) {
      return friendship;
    }

    @Override
    public void deleteFriendship(UUID userId, UUID requesterId) {
      if (failOnDelete) {
        throw new IllegalStateException("Friendship deletion failed.");
      }
      deletedUserId = userId;
      deletedRequesterId = requesterId;
    }
  }

  private static class FakeNotifications implements NotificationEventGateway {
    private int deniedCount;
    private UUID senderId;
    private UUID receiverId;
    private boolean failOnDeny;

    @Override
    public void friendRequestSent(UUID senderId, UUID receiverId) {
      fail("Unexpected friend request sent notification.");
    }

    @Override
    public void friendRequestDenied(UUID senderId, UUID receiverId) {
      deniedCount++;
      this.senderId = senderId;
      this.receiverId = receiverId;
      if (failOnDeny) {
        throw new IllegalStateException("Notification unavailable.");
      }
    }

    @Override
    public void expenseAdded(Expense expense) {
      fail("Unexpected expense notification.");
    }
  }

  private static class RecordingPresenter
      implements DenyFriendRequestOutputBoundary {

    private DenyFriendRequestOutputData success;
    private String failure;

    @Override
    public void prepareSuccessView(DenyFriendRequestOutputData data) {
      success = data;
    }

    @Override
    public void prepareFailView(String message) {
      failure = message;
    }
  }
}
