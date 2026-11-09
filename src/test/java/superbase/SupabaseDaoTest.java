package superbase;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SupabaseDaoTest {
    private SupabaseUserDao users;
    private SupabaseFriendshipDao friendships;
    private SupabaseNotificationDao notifications;
    private UUID userA;
    private UUID userB;
    private String nameA;
    private static final List<UUID> CREATED_USERS = new ArrayList<>();

    /**
     * Creates isolated users and DAO instances for each database test.
     *
     * @throws Exception if the test database setup fails
     */
    @BeforeEach
    void setUp() throws Exception {
        Assumptions.assumeTrue(System.getenv("SUPABASE_DB_URL") != null
                        && System.getenv("SUPABASE_DB_USER") != null
                        && System.getenv("SUPABASE_DB_PASSWORD") != null,
                "Supabase env vars not set; skipping");
        PostgresDataSource source = new PostgresDataSource();
        users = new SupabaseUserDao(source);
        friendships = new SupabaseFriendshipDao(source);
        notifications = new SupabaseNotificationDao(source);
        nameA = "test_" + UUID.randomUUID();
        String nameB = "test_" + UUID.randomUUID();
        userA = users.save(nameA, "hash", "A", "Tester");
        userB = users.save(nameB, "hash", "B", "Tester");
        CREATED_USERS.add(userA);
        CREATED_USERS.add(userB);
    }

    /**
     * Optionally removes all users created by this test class.
     *
     * <p>Run {@code mvn -Dtest=SupabaseDaoTest test} from a terminal. After the
     * tests finish, review the printed user IDs and type {@code delete} followed
     * by Enter to remove the test users. Press Enter without typing {@code delete}
     * to keep the users in the database for inspection in Supabase.</p>
     *
     * @throws Exception if the test database cleanup fails
     */
    @AfterAll
    static void cleanUpWhenRequested() throws Exception {
        if (CREATED_USERS.isEmpty()) {
            return;
        }

        System.out.println("Created test users:");
        CREATED_USERS.forEach(userId -> System.out.println("  " + userId));
        System.out.print("Type 'delete' and press Enter to remove them, or press Enter to keep them: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String command = reader.readLine();
        if ("delete".equalsIgnoreCase(command == null ? "" : command.trim())) {
            SupabaseUserDao users = new SupabaseUserDao(new PostgresDataSource());
            for (UUID userId : CREATED_USERS) {
                users.deleteById(userId);
            }
            System.out.println("Test users deleted.");
        } else {
            System.out.println("Test users kept in the database.");
        }
    }

    /**
     * Verifies that a saved user can be loaded by username.
     *
     * @throws Exception if the database operation fails
     */
    @Test
    void savedUserCanBeReadBack() throws Exception {
        var row = users.findByUsername(nameA).orElseThrow();
        assertEquals(userA, row.id());
        assertEquals("hash", row.passwordHash());
    }

    /**
     * Verifies that duplicate usernames are rejected by the database.
     */
    @Test
    void duplicateUsernameFails() {
        assertThrows(java.sql.SQLException.class, () -> users.save(nameA, "h", "x", "y"));
    }

    /**
     * Verifies that a sent friend request can be accepted.
     *
     * @throws Exception if a database operation fails
     */
    @Test
    void friendRequestCanBeAccepted() throws Exception {
        UUID id = friendships.sendRequest(userA, userB);
        assertEquals("SENT_REQUEST", friendships.getStatus(id));
        friendships.accept(id);
        assertEquals("ADDED", friendships.getStatus(id));
    }

    /**
     * Verifies that notifications are delivered and can be marked as read.
     *
     * @throws Exception if a database operation fails
     */
    @Test
    void notificationReachesReceiverAndCanBeMarkedRead() throws Exception {
        notifications.send(userA, userB, "FRIEND_REQUEST", "{\"hello\":true}");
        List<SupabaseNotificationDao.NotificationRow> inbox = notifications.getUnread(userB);
        assertEquals(1, inbox.size());
        assertEquals("FRIEND_REQUEST", inbox.get(0).type());
        assertTrue(notifications.getUnread(userA).isEmpty());   // sender has nothing
        notifications.markRead(inbox.get(0).id());
        assertTrue(notifications.getUnread(userB).isEmpty());
    }
}