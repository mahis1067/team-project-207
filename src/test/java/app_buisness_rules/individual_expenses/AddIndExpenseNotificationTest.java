package app_buisness_rules.individual_expenses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import app_buisness_rules.notifications.NotificationEventGateway;
import entities.expenses.Expense;
import entities.users.User;
import interface_adapters.session.InMemorySessionState;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Tests notification publishing during one-to-one expense creation. */
class AddIndExpenseNotificationTest {

  @Test
  void successfulExpensePublishesSavedExpense() {
    TestContext context = new TestContext();

    context.execute("Groceries", 10.10);

    assertNotNull(context.dataAccess.savedExpense);
    assertEquals(1, context.notifications.addedCount);
    assertSame(
        context.dataAccess.savedExpense,
        context.notifications.publishedExpense);
    assertEquals(
        context.payer.getUserId(),
        context.notifications.publishedExpense.getPayer().getUserId());
    assertEquals(
        context.other.getUserId(),
        context.notifications.publishedExpense
            .getParticipants().get(1).getUserId());
    assertNotNull(context.presenter.success);
    assertNull(context.presenter.failure);
  }

  @Test
  void invalidExpenseDoesNotPublishNotification() {
    TestContext context = new TestContext();

    context.execute("Groceries", -5.00);

    assertNull(context.dataAccess.savedExpense);
    assertEquals(0, context.notifications.addedCount);
    assertNull(context.presenter.success);
    assertNotNull(context.presenter.failure);
  }

  @Test
  void failedExpenseSaveDoesNotPublishNotification() {
    TestContext context = new TestContext();
    context.dataAccess.failOnSave = true;

    context.execute("Groceries", 10.10);

    assertNull(context.dataAccess.savedExpense);
    assertEquals(0, context.notifications.addedCount);
    assertNull(context.presenter.success);
    assertEquals("Unable to save the expense.", context.presenter.failure);
  }

  @Test
  void notificationFailureDoesNotUndoSavedExpense() {
    TestContext context = new TestContext();
    context.notifications.failOnAdd = true;

    context.execute("Groceries", 10.10);

    assertNotNull(context.dataAccess.savedExpense);
    assertEquals(1, context.notifications.addedCount);
    assertNotNull(context.presenter.success);
    assertNull(context.presenter.failure);
  }

  private static class TestContext {
    private final User payer = new User(
        UUID.randomUUID(), "payer", "", "", "", new ArrayList<>());
    private final User other = new User(
        UUID.randomUUID(), "other", "", "", "", new ArrayList<>());
    private final InMemorySessionState session = new InMemorySessionState();
    private final FakeExpenseDataAccess dataAccess = new FakeExpenseDataAccess();
    private final FakeNotifications notifications = new FakeNotifications();
    private final RecordingPresenter presenter = new RecordingPresenter();

    private TestContext() {
      session.setCurrentUserId(payer.getUserId());
    }

    private void execute(String description, double amount) {
      AddIndExpenseInteractor interactor = new AddIndExpenseInteractor(
          dataAccess, session, presenter, notifications);
      interactor.execute(new AddIndExpenseInputData(
          other.getUserId(), description, amount));
    }

    private class FakeExpenseDataAccess
        implements AddIndExpenseDataAccessInterface {

      private Expense savedExpense;
      private boolean failOnSave;

      @Override
      public User findUser(UUID id) {
        if (payer.getUserId().equals(id)) {
          return payer;
        }
        if (other.getUserId().equals(id)) {
          return other;
        }
        return null;
      }

      @Override
      public void saveExpense(Expense expense) {
        if (failOnSave) {
          throw new IllegalStateException("Expense database unavailable.");
        }
        savedExpense = expense;
      }
    }
  }

  private static class FakeNotifications implements NotificationEventGateway {
    private int addedCount;
    private Expense publishedExpense;
    private boolean failOnAdd;

    @Override
    public void friendRequestSent(UUID senderId, UUID receiverId) {
      fail("Unexpected friend request notification.");
    }

    @Override
    public void friendRequestDenied(UUID senderId, UUID receiverId) {
      fail("Unexpected friend request denial notification.");
    }

    @Override
    public void expenseAdded(Expense expense) {
      addedCount++;
      publishedExpense = expense;
      if (failOnAdd) {
        throw new IllegalStateException("Notification unavailable.");
      }
    }
  }

  private static class RecordingPresenter
      implements AddIndExpenseOutputBoundary {

    private AddIndExpenseOutputData success;
    private String failure;

    @Override
    public void prepareSuccessView(AddIndExpenseOutputData data) {
      success = data;
    }

    @Override
    public void prepareFailView(String message) {
      failure = message;
    }
  }
}
