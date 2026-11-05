package entities.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Tests the Friendship entity. */
class FriendshipTest {

  /** Tests that the constructor initializes the friendship fields. */
  @Test
  void constructorInitializesFields() {
    User sender =
        new User(UUID.randomUUID(), "sender", "hash1", "First", "Sender", new ArrayList<>());
    User receiver =
        new User(UUID.randomUUID(), "receiver", "hash2", "Second", "Receiver", new ArrayList<>());

    LocalDateTime addedAt = LocalDateTime.of(2026, 11, 4, 12, 0);

    Friendship friendship =
        new Friendship(sender, receiver, FriendshipStatus.SENT_REQUEST, addedAt);

    assertSame(sender, friendship.getRequestedBy());
    assertSame(receiver, friendship.getSentTo());
    assertEquals(FriendshipStatus.SENT_REQUEST, friendship.getStatus());
    assertEquals(addedAt, friendship.getAddedAt());
  }

  /** Tests that all friendship status values can be stored. */
  @Test
  void supportsAllFriendshipStatuses() {
    User sender =
        new User(UUID.randomUUID(), "sender", "hash1", "First", "Sender", new ArrayList<>());
    User receiver =
        new User(UUID.randomUUID(), "receiver", "hash2", "Second", "Receiver", new ArrayList<>());

    LocalDateTime addedAt = LocalDateTime.of(2026, 11, 4, 12, 0);

    for (FriendshipStatus status : FriendshipStatus.values()) {
      Friendship friendship = new Friendship(sender, receiver, status, addedAt);

      assertEquals(status, friendship.getStatus());
    }
  }
}
