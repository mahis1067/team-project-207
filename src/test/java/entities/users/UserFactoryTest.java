package entities.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Tests user creation from persisted fields. */
class UserFactoryTest {

  /** Creates a user and keeps its friendship IDs isolated from the input list. */
  @Test
  void createBuildsUserAndCopiesFriendships() {
    UUID userId = UUID.randomUUID();
    UUID friendId = UUID.randomUUID();
    List<UUID> friendships = new ArrayList<>(List.of(friendId));

    User user = UserFactory.create(
        userId, "username", "password-hash", "First", "Last", friendships);
    friendships.clear();

    assertEquals(userId, user.getUserId());
    assertEquals("username", user.getUsername());
    assertEquals("password-hash", user.getPasswordHash());
    assertEquals("First", user.getFirstName());
    assertEquals("Last", user.getLastName());
    assertEquals(List.of(friendId), user.getFriendships());
  }

  /** Rejects a missing persisted user ID or friendship list. */
  @Test
  void createRejectsMissingRequiredFields() {
    assertThrows(
        IllegalArgumentException.class,
        () -> UserFactory.create(
            null, "username", "hash", "First", "Last", List.of()));
    assertThrows(
        IllegalArgumentException.class,
        () -> UserFactory.create(
            UUID.randomUUID(), "username", "hash", "First", "Last", null));
  }
}
