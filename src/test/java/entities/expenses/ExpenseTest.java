package entities.expenses;

import static org.junit.jupiter.api.Assertions.assertEquals;

import entities.users.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Tests the Expense entity.
 */
class ExpenseTest {

  /**
   * Tests that constructor correctly initializes expenses.
   */
  @Test
  void constructorInitializesFields() {
    UUID expenseId = UUID.randomUUID();

    User creator = new User(UUID.randomUUID(), "creator", "hash", "Test", "Creator", new ArrayList<>());
    User payer = new User(UUID.randomUUID(), "payer", "hash", "Test", "Payer", new ArrayList<>());

    List<User> participants = new ArrayList<>();
    participants.add(creator);
    participants.add(payer);

    LocalDateTime dateCreated = LocalDateTime.of(2026, 10, 10, 10, 10);

    Expense expense =
            new Expense(
                    expenseId,
                    "Groceries",
                    10.10,
                    creator,
                    payer,
                    participants,
                    dateCreated,
                    "pending");

    assertEquals(expenseId, expense.getExpenseId());
    assertEquals("Groceries", expense.getDescription());
    assertEquals(10.10, expense.getAmount());
    assertEquals(creator, expense.getCreatedBy());
    assertEquals(payer, expense.getPayer());
    assertEquals(participants, expense.getParticipants());
    assertEquals(dateCreated, expense.getDateCreated());
    assertEquals("pending", expense.getStatusAll());
  }

  /**
   * Tests that changes to the participants list do not affect the expense.
   */
  @Test
  void constructorCopiesParticipantsList() {
    User user = new User(UUID.randomUUID(), "testuser", "hash", "Test", "User", new ArrayList<>());

    List<User> participants = new ArrayList<>();
    participants.add(user);

    Expense expense =
            new Expense(
                    UUID.randomUUID(),
                    "Groceries",
                    10.10,
                    user,
                    user,
                    participants,
                    LocalDateTime.of(2026, 10, 10, 10, 10),
                    "pending");

    participants.clear();

    assertEquals(1, expense.getParticipants().size());
    assertEquals(user, expense.getParticipants().get(0));
  }

  /**
   * Tests that modifying a returned list does not change the expense.
   */
  @Test
  void getterReturnsParticipantsListCopy() {
    User user = new User(UUID.randomUUID(), "testuser", "hash", "Test", "User", new ArrayList<>());

    List<User> participants = new ArrayList<>();
    participants.add(user);

    Expense expense =
            new Expense(
                    UUID.randomUUID(),
                    "Groceries",
                    10.10,
                    user,
                    user,
                    participants,
                    LocalDateTime.of(2026, 10, 10, 10, 10),
                    "pending");

    expense.getParticipants().clear();

    assertEquals(1, expense.getParticipants().size());
    assertEquals(user, expense.getParticipants().get(0));
  }
}