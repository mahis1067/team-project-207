package superbase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entities.expenses.Expense;
import entities.users.User;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Tests database notification mapping without requiring live Supabase. */
class SupabaseNotificationEventGatewayTest {

  @Test
  void friendRequestSentMapsTypeAndRecipients() {
    RecordingSink sink = new RecordingSink();
    UUID sender = UUID.randomUUID();
    UUID receiver = UUID.randomUUID();

    new SupabaseNotificationEventGateway(sink)
        .friendRequestSent(sender, receiver);

    assertEquals(1, sink.count);
    assertEquals(sender, sink.sender);
    assertEquals(receiver, sink.receiver);
    assertEquals("NEW_FRIEND_REQUEST", sink.type);
    assertTrue(sink.payload.contains(sender.toString()));
  }

  @Test
  void friendRequestDeniedMapsTypeAndRecipients() {
    RecordingSink sink = new RecordingSink();
    UUID denier = UUID.randomUUID();
    UUID requester = UUID.randomUUID();

    new SupabaseNotificationEventGateway(sink)
        .friendRequestDenied(denier, requester);

    assertEquals(denier, sink.sender);
    assertEquals(requester, sink.receiver);
    assertEquals("FRIEND_REQUEST_DENIED", sink.type);
    assertEquals(1, sink.count);
  }

  @Test
  void expenseAddedMapsReceiverAndEscapesDescription() {
    RecordingSink sink = new RecordingSink();
    User payer = user("payer");
    User other = user("other");
    UUID expenseId = UUID.randomUUID();
    Expense expense = new Expense(
        expenseId, "Pizza \"night\"\nwith friends", 12.50,
        payer, payer, List.of(payer, other),
        LocalDateTime.now(), "pending");

    new SupabaseNotificationEventGateway(sink).expenseAdded(expense);

    assertEquals(1, sink.count);
    assertEquals(payer.getUserId(), sink.sender);
    assertEquals(other.getUserId(), sink.receiver);
    assertEquals("NEW_EXPENSE_ADDED", sink.type);
    assertTrue(sink.payload.contains(expenseId.toString()));
    assertTrue(sink.payload.contains("12.5"));
    assertTrue(sink.payload.contains("\\\"night\\\""));
    assertTrue(sink.payload.contains("\\nwith friends"));
    assertFalse(sink.payload.contains("night\"\nwith"));
  }

  @Test
  void sqlFailureIsWrappedWithOriginalCause() {
    SupabaseNotificationEventGateway gateway =
        new SupabaseNotificationEventGateway(
            (sender, receiver, type, payload) -> {
              throw new SQLException("Database unavailable.");
            });

    IllegalStateException failure = assertThrows(
        IllegalStateException.class,
        () -> gateway.friendRequestSent(
            UUID.randomUUID(), UUID.randomUUID()));

    assertNotNull(failure.getCause());
    assertTrue(failure.getCause() instanceof SQLException);
  }

  @Test
  void invalidExpenseDoesNotWriteNotification() {
    RecordingSink sink = new RecordingSink();
    User payer = user("payer");
    Expense expense = new Expense(
        UUID.randomUUID(), "Invalid", 5,
        payer, payer, List.of(payer, payer),
        LocalDateTime.now(), "pending");

    assertThrows(IllegalArgumentException.class,
        () -> new SupabaseNotificationEventGateway(sink)
            .expenseAdded(expense));

    assertEquals(0, sink.count);
  }

  private static User user(String username) {
    return new User(
        UUID.randomUUID(), username, "", "", "",
        new java.util.ArrayList<>());
  }

  private static class RecordingSink
      implements SupabaseNotificationEventGateway.NotificationSink {

    private int count;
    private UUID sender;
    private UUID receiver;
    private String type;
    private String payload;

    @Override
    public void send(UUID senderId, UUID receiverId,
                     String notificationType, String payloadJson) {
      count++;
      sender = senderId;
      receiver = receiverId;
      type = notificationType;
      payload = payloadJson;
    }
  }
}
