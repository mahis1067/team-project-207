package interface_adapters.session;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemorySessionStateTest {

    @Test
    void testInitiallyLoggedOut() {
        InMemorySessionState session = new InMemorySessionState();

        assertNull(session.getCurrentUserId());
    }

    @Test
    void testSetCurrentUser() {
        InMemorySessionState session = new InMemorySessionState();
        UUID userId = UUID.randomUUID();

        session.setCurrentUserId(userId);

        assertEquals(userId, session.getCurrentUserId());
    }

    @Test
    void testClearSession() {
        InMemorySessionState session = new InMemorySessionState();
        session.setCurrentUserId(UUID.randomUUID());

        session.clear();

        assertNull(session.getCurrentUserId());
    }

    @Test
    void testRejectNullUserId() {
        InMemorySessionState session = new InMemorySessionState();

        assertThrows(IllegalArgumentException.class,
                () -> session.setCurrentUserId(null));
        assertNull(session.getCurrentUserId());
    }
}
