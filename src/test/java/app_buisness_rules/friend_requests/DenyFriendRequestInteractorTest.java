package app_buisness_rules.friend_requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import entities.users.Friendship;
import entities.users.FriendshipStatus;
import entities.users.User;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Tests the deny-friend-request interactor. */
class DenyFriendRequestInteractorTest {
  /** Deletes the friendship and presents success. */
  @Test
  void executeDeletesFriendshipAndPresentsSuccess() {
    InMemoryDataAccess dataAccess = new InMemoryDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();
    DenyFriendRequestInteractor interactor =
        new DenyFriendRequestInteractor(dataAccess, presenter);
    UUID userId = UUID.randomUUID();
    UUID requesterId = UUID.randomUUID();
    dataAccess.friendship = pendingFriendship(userId, requesterId);

    interactor.execute(new DenyFriendRequestInputData(userId, requesterId));

    assertEquals(userId, dataAccess.deletedUserId);
    assertEquals(requesterId, dataAccess.deletedRequesterId);
    assertEquals(requesterId, presenter.output.requesterId());
  }

  /** Rejects input when no user id is supplied. */
  @Test
  void executeRejectsMissingUserId() {
    InMemoryDataAccess dataAccess = new InMemoryDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();
    DenyFriendRequestInteractor interactor =
        new DenyFriendRequestInteractor(dataAccess, presenter);

    interactor.execute(
        new DenyFriendRequestInputData(null, UUID.randomUUID()));

    assertNull(dataAccess.deletedUserId);
    assertEquals("User id is required.", presenter.failureMessage);
  }

  /** Rejects input when no requester id is supplied. */
  @Test
  void executeRejectsMissingRequesterId() {
    InMemoryDataAccess dataAccess = new InMemoryDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();
    DenyFriendRequestInteractor interactor =
        new DenyFriendRequestInteractor(dataAccess, presenter);

    interactor.execute(new DenyFriendRequestInputData(UUID.randomUUID(), null));

    assertNull(dataAccess.deletedRequesterId);
    assertEquals("Requester id is required.", presenter.failureMessage);
  }

  /** Presents not-found failures to the output boundary. */
  @Test
  void executePresentsNotFoundFailure() {
    InMemoryDataAccess dataAccess = new InMemoryDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();
    DenyFriendRequestInteractor interactor =
        new DenyFriendRequestInteractor(dataAccess, presenter);

    interactor.execute(
        new DenyFriendRequestInputData(UUID.randomUUID(), UUID.randomUUID()));

    assertNull(dataAccess.deletedUserId);
    assertEquals("Friend request not found.", presenter.failureMessage);
  }

  /** Presents persistence failures to the output boundary. */
  @Test
  void executePresentsDataAccessFailure() {
    RecordingPresenter presenter = new RecordingPresenter();
    DenyFriendRequestInteractor interactor =
        new DenyFriendRequestInteractor(
            new DenyFriendRequestDataAccessInterface() {
              @Override
              public Friendship getFriendship(UUID userId, UUID requesterId) {
                throw new IllegalStateException("Database unavailable.");
              }

              @Override
              public boolean deleteFriendship(
                  UUID userId, UUID requesterId) {
                return true;
              }
            },
            presenter);

    interactor.execute(
        new DenyFriendRequestInputData(UUID.randomUUID(), UUID.randomUUID()));

    assertEquals("Database unavailable.", presenter.failureMessage);
  }

  private static Friendship pendingFriendship(
      UUID userId, UUID requesterId) {
    User requester = new User(requesterId, "", "", "", "", new ArrayList<>());
    User receiver = new User(userId, "", "", "", "", new ArrayList<>());
    return new Friendship(
        requester, receiver, FriendshipStatus.SENT_REQUEST, null);
  }

  private static class InMemoryDataAccess
      implements DenyFriendRequestDataAccessInterface {
    private Friendship friendship;
    private UUID deletedUserId;
    private UUID deletedRequesterId;

    @Override
    public Friendship getFriendship(UUID userId, UUID requesterId) {
      return friendship;
    }

    @Override
    public boolean deleteFriendship(UUID userId, UUID requesterId) {
      deletedUserId = userId;
      deletedRequesterId = requesterId;
      return true;
    }
  }

  private static class RecordingPresenter
      implements DenyFriendRequestOutputBoundary {
    private DenyFriendRequestOutputData output;
    private String failureMessage;

    @Override
    public void prepareSuccessView(DenyFriendRequestOutputData data) {
      output = data;
    }

    @Override
    public void prepareFailView(String message) {
      failureMessage = message;
    }
  }
}
