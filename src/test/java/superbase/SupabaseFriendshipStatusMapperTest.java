package superbase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import entities.users.FriendshipStatus;
import org.junit.jupiter.api.Test;

class SupabaseFriendshipStatusMapperTest {
    @Test
    void mapsPendingToSentRequest() {
        assertEquals(
                FriendshipStatus.SENT_REQUEST,
                SupabaseFriendshipStatusMapper.toDomainStatus("PENDING"));
    }

    @Test
    void mapsAddedStatus() {
        assertEquals(
                FriendshipStatus.ADDED,
                SupabaseFriendshipStatusMapper.toDomainStatus("ADDED"));
    }

    @Test
    void rejectsUnsupportedDatabaseStatus() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SupabaseFriendshipStatusMapper.toDomainStatus("SENT_REQUEST"));
    }
}
