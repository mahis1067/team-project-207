package entities.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Tests friendship creation from persisted fields. */
class FriendshipFactoryTest {

  /** Preserves the users, status, and timestamp supplied to the factory. */
  @Test
  void createBuildsFriendship() {
    User sender = user("sender");
    User receiver = user("receiver");
    LocalDateTime addedAt = LocalDateTime.of(2026, 11, 4, 12, 0);

    Friendship friendship = FriendshipFactory.create(
        sender, receiver, FriendshipStatus.ADDED, addedAt);

    assertSame(sender, friendship.getRequestedBy());
    assertSame(receiver, friendship.getSentTo());
    assertEquals(FriendshipStatus.ADDED, friendship.getStatus());
    assertEquals(addedAt, friendship.getAddedAt());
  }

  private static User user(String username) {
    return UserFactory.create(
        UUID.randomUUID(), username, "hash", "First", "Last", List.of());
  }
}
