package superbase;

import app_buisness_rules.notifications.NotificationEventGateway;
import entities.expenses.Expense;
import entities.users.User;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Persists application notification events through Supabase.
 *
 * <p>Payload fields are provisional until the notification
 * entity and factory contract is finalized.
 */
public class SupabaseNotificationEventGateway
    implements NotificationEventGateway {

  @FunctionalInterface
  interface NotificationSink {
    void send(UUID senderId, UUID receiverId,
              String type, String payload) throws SQLException;
  }

  private final NotificationSink sink;

  /**
   * Creates the production database gateway.
   *
   * @param source PostgreSQL connection provider
   */
  public SupabaseNotificationEventGateway(PostgresDataSource source) {
    this(new SupabaseNotificationDao(source)::send);
  }

  /** Creates a gateway with an injectable persistence sink for tests. */
  SupabaseNotificationEventGateway(NotificationSink sink) {
    this.sink = java.util.Objects.requireNonNull(sink);
  }

  @Override
  public void friendRequestSent(UUID senderId, UUID receiverId) {
    validateUsers(senderId, receiverId);
    publish(senderId, receiverId, "NEW_FRIEND_REQUEST",
        "{\"senderId\":" + jsonString(senderId.toString()) + "}");
  }

  @Override
  public void friendRequestDenied(UUID senderId, UUID receiverId) {
    validateUsers(senderId, receiverId);
    publish(senderId, receiverId, "FRIEND_REQUEST_DENIED",
        "{\"senderId\":" + jsonString(senderId.toString()) + "}");
  }

  @Override
  public void expenseAdded(Expense expense) {
    if (expense == null || expense.getExpenseId() == null
        || expense.getPayer() == null) {
      throw new IllegalArgumentException("Expense and payer are required.");
    }

    UUID payerId = expense.getPayer().getUserId();
    if (payerId == null || !Double.isFinite(expense.getAmount())) {
      throw new IllegalArgumentException("Invalid expense notification data.");
    }

    List<User> participants = expense.getParticipants();
    if (participants.size() != 2) {
      throw new IllegalArgumentException(
          "One-to-one expense requires two participants.");
    }

    UUID receiverId = null;
    boolean payerPresent = false;
    for (User participant : participants) {
      if (participant == null || participant.getUserId() == null) {
        throw new IllegalArgumentException("Invalid expense participant.");
      }
      UUID participantId = participant.getUserId();
      if (participantId.equals(payerId)) {
        payerPresent = true;
      } else {
        if (receiverId != null) {
          throw new IllegalArgumentException(
              "Expense must have one other participant.");
        }
        receiverId = participantId;
      }
    }

    if (!payerPresent || receiverId == null) {
      throw new IllegalArgumentException(
          "Expense must include payer and one other participant.");
    }

    String payload = "{"
        + "\"expenseId\":" + jsonString(expense.getExpenseId().toString())
        + ",\"description\":" + jsonString(expense.getDescription())
        + ",\"amount\":" + Double.toString(expense.getAmount())
        + ",\"payerId\":" + jsonString(payerId.toString())
        + ",\"otherParticipantId\":" + jsonString(receiverId.toString())
        + "}";

    publish(payerId, receiverId, "NEW_EXPENSE_ADDED", payload);
  }

  private static void validateUsers(UUID senderId, UUID receiverId) {
    if (senderId == null || receiverId == null
        || senderId.equals(receiverId)) {
      throw new IllegalArgumentException(
          "Notification requires two different users.");
    }
  }

  private void publish(UUID senderId, UUID receiverId,
                       String type, String payload) {
    validateUsers(senderId, receiverId);

    try {
      sink.send(senderId, receiverId, type, payload);
    } catch (SQLException exception) {
      throw new IllegalStateException(
          "Unable to persist notification.", exception);
    }
  }

  /** Escapes a Java string for inclusion in a JSON value. */
  private static String jsonString(String value) {
    if (value == null) {
      return "null";
    }

    StringBuilder result = new StringBuilder("\"");
    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      switch (ch) {
        case '"' -> result.append("\\\"");
        case '\\' -> result.append("\\\\");
        case '\n' -> result.append("\\n");
        case '\r' -> result.append("\\r");
        case '\t' -> result.append("\\t");
        default -> {
          if (ch < 0x20) {
            result.append(String.format("\\u%04x", (int) ch));
          } else {
            result.append(ch);
          }
        }
      }
    }
    return result.append('"').toString();
  }
}
