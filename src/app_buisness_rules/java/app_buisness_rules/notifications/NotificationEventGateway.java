package app_buisness_rules.notifications;

import entities.expenses.Expense;
import java.util.UUID;

/**
 * Application boundary for publishing notification-worthy business events.
 *
 * <p>The implementation is responsible for mapping these events to
 * notification entities and persisting them.
 */
public interface NotificationEventGateway {

  /**
   * Publishes a newly sent friend request.
   *
   * @param senderId the requesting user
   * @param receiverId the receiving user
   */
  void friendRequestSent(UUID senderId, UUID receiverId);

  /**
   * Publishes a denied friend request.
   *
   * @param senderId the user who denied the request
   * @param receiverId the original requester
   */
  void friendRequestDenied(UUID senderId, UUID receiverId);

  /**
   * Publishes a newly created one-to-one expense.
   *
   * @param expense the successfully persisted expense
   */
  void expenseAdded(Expense expense);
}
