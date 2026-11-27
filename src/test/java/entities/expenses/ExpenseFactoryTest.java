package entities.expenses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import entities.users.User;
import entities.users.UserFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Tests expense creation from persisted fields. */
class ExpenseFactoryTest {

  /** Preserves all expense fields and participants supplied to the factory. */
  @Test
  void createBuildsExpense() {
    UUID expenseId = UUID.randomUUID();
    User creator = user("creator");
    User participant = user("participant");
    List<User> participants = List.of(creator, participant);
    LocalDateTime createdAt = LocalDateTime.of(2026, 11, 4, 12, 0);

    Expense expense = ExpenseFactory.create(new ExpenseDetails(
        expenseId, "Groceries", 12.50, creator, creator, participants, createdAt, "pending"));

    assertEquals(expenseId, expense.getExpenseId());
    assertEquals("Groceries", expense.getDescription());
    assertEquals(12.50, expense.getAmount());
    assertSame(creator, expense.getCreatedBy());
    assertSame(creator, expense.getPayer());
    assertEquals(participants, expense.getParticipants());
    assertEquals(createdAt, expense.getDateCreated());
    assertEquals("pending", expense.getStatusAll());
  }

  private static User user(String username) {
    return UserFactory.create(
        UUID.randomUUID(), username, "hash", "First", "Last", List.of());
  }
}
