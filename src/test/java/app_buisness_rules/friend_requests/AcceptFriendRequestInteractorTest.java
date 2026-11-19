package app_buisness_rules.friend_requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Tests the accept-friend-request interactor. */
class AcceptFriendRequestInteractorTest {
  /** Updates the friendship and presents success. */
  @Test
  void executeUpdatesFriendshipAndPresentsSuccess() {
    InMemoryDataAccess dataAccess = new InMemoryDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();
    AcceptFriendRequestInteractor interactor =
        new AcceptFriendRequestInteractor(dataAccess, presenter);
    UUID friendshipId = UUID.randomUUID();

    interactor.execute(new AcceptFriendRequestInputData(friendshipId));

    assertEquals(friendshipId, dataAccess.updatedFriendshipId);
    assertEquals(friendshipId, presenter.output.friendshipId());
    assertEquals("ADDED", presenter.output.status());
  }

  /** Rejects input when no friendship id is supplied. */
  @Test
  void executeRejectsMissingFriendshipId() {
    InMemoryDataAccess dataAccess = new InMemoryDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();
    AcceptFriendRequestInteractor interactor =
        new AcceptFriendRequestInteractor(dataAccess, presenter);

    interactor.execute(new AcceptFriendRequestInputData(null));

    assertNull(dataAccess.updatedFriendshipId);
    assertEquals("Friendship id is required.", presenter.failureMessage);
  }

  /** Presents persistence failures to the output boundary. */
  @Test
  void executePresentsDataAccessFailure() {
    RecordingPresenter presenter = new RecordingPresenter();
    AcceptFriendRequestInteractor interactor =
        new AcceptFriendRequestInteractor(
            friendshipId -> {
              throw new IllegalStateException("Database unavailable.");
            },
            presenter);

    interactor.execute(new AcceptFriendRequestInputData(UUID.randomUUID()));

    assertEquals("Database unavailable.", presenter.failureMessage);
  }

  private static class InMemoryDataAccess
      implements AcceptFriendRequestDataAccessInterface {
    private UUID updatedFriendshipId;

    @Override
    public void updateFriendship(UUID friendshipId) {
      updatedFriendshipId = friendshipId;
    }
  }

  private static class RecordingPresenter
      implements AcceptFriendRequestOutputBoundary {
    private AcceptFriendRequestOutputData output;
    private String failureMessage;

    @Override
    public void prepareSuccessView(AcceptFriendRequestOutputData data) {
      output = data;
    }

    @Override
    public void prepareFailView(String message) {
      failureMessage = message;
    }
  }
}
