package entities.users;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Tests the User entity. */
class UserTest {

  /** Tests that the constructor correctly initializes user information. */
  @Test
  void constructorInitializesFields() {
    UUID userId = UUID.randomUUID();
    UUID friendId = UUID.randomUUID();

    List<UUID> friends = new ArrayList<>();
    friends.add(friendId);

    User user = new User(userId, "testuser", "hashedPassword", "Test", "User", friends);

    assertEquals(userId, user.getUserId());
    assertEquals("testuser", user.getUsername());
    assertEquals("hashedPassword", user.getPasswordHash());
    assertEquals("Test", user.getFirstName());
    assertEquals("User", user.getLastName());
    assertEquals(friends, user.getFriendships());
  }

  /** Tests that changes to the original list do not affect the user. */
  @Test
  void constructorCopiesFriendshipList() {
    List<UUID> friends = new ArrayList<>();
    UUID friendId = UUID.randomUUID();
    friends.add(friendId);

    User user = new User(UUID.randomUUID(), "testuser", "hash", "Test", "User", friends);

    friends.clear();

    assertEquals(1, user.getFriendships().size());
    assertEquals(friendId, user.getFriendships().get(0));
  }

  /** Tests that modifying the returned list does not affect the user. */
  @Test
  void getterReturnsFriendshipListCopy() {
    UUID friendId = UUID.randomUUID();
    List<UUID> friends = new ArrayList<>();
    friends.add(friendId);

    User user = new User(UUID.randomUUID(), "testuser", "hash", "Test", "User", friends);

    user.getFriendships().clear();

    assertEquals(1, user.getFriendships().size());
    assertEquals(friendId, user.getFriendships().get(0));
  }
}
