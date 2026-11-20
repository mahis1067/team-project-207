package interface_adapters.persistence;

import entities.users.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void testMapsPersistedFields() {
        UUID userId = UUID.randomUUID();

        User user = UserMapper.toUser(
                userId,
                "testuser",
                "stored_hash",
                "Test",
                "User",
                Collections.emptyList());

        assertEquals(userId, user.getUserId());
        assertEquals("testuser", user.getUsername());
        assertEquals("stored_hash", user.getPasswordHash());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
    }

    @Test
    void testMapsFriendships() {
        UUID friendId = UUID.randomUUID();

        User user = UserMapper.toUser(
                UUID.randomUUID(),
                "testuser",
                "stored_hash",
                "Test",
                "User",
                Collections.singletonList(friendId));

        assertEquals(Collections.singletonList(friendId),
                user.getFriendships());
    }

    @Test
    void testCopiesFriendshipList() {
        List<UUID> friendships = new ArrayList<>();
        friendships.add(UUID.randomUUID());

        User user = UserMapper.toUser(
                UUID.randomUUID(),
                "testuser",
                "stored_hash",
                "Test",
                "User",
                friendships);

        friendships.clear();

        assertEquals(1, user.getFriendships().size());
    }

    @Test
    void testRejectsNullUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> UserMapper.toUser(
                        null, "testuser", "hash", "Test", "User",
                        Collections.emptyList()));
    }

    @Test
    void testRejectsNullFriendships() {
        assertThrows(IllegalArgumentException.class,
                () -> UserMapper.toUser(
                        UUID.randomUUID(), "testuser", "hash",
                        "Test", "User", null));
    }
}
