package frameworks_and_drivers.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Pbkdf2PasswordHasherTest {

    private final Pbkdf2PasswordHasher hasher =
            new Pbkdf2PasswordHasher();

    @Test
    void testCorrectPassword() {
        String storedHash = hasher.hash("password123");

        assertTrue(hasher.verify("password123", storedHash));
    }

    @Test
    void testIncorrectPassword() {
        String storedHash = hasher.hash("password123");

        assertFalse(hasher.verify("wrongpassword", storedHash));
    }

    @Test
    void testUniqueSalts() {
        String first = hasher.hash("samepassword");
        String second = hasher.hash("samepassword");

        assertNotEquals(first, second);
        assertTrue(hasher.verify("samepassword", first));
        assertTrue(hasher.verify("samepassword", second));
    }

    @Test
    void testInvalidStoredHash() {
        assertFalse(hasher.verify("password123", null));
        assertFalse(hasher.verify("password123", "invalid"));
        assertFalse(hasher.verify(null, "invalid"));
        assertFalse(hasher.verify("password123",
                "pbkdf2_sha256$bad$value$value"));
    }

    @Test
    void testRejectEmptyPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> hasher.hash(""));
        assertThrows(IllegalArgumentException.class,
                () -> hasher.hash(null));
    }
}
