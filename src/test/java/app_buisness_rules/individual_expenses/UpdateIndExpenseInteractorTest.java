package app_buisness_rules.individual_expenses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import interface_adapters.individual_expenses.UpdateIndExpenseController;
import interface_adapters.individual_expenses.UpdateIndExpensePresenter;
import interface_adapters.individual_expenses.UpdateIndExpenseViewModel;
import interface_adapters.session.InMemorySessionState;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests the update expense use case through its controller and presenter. */
class UpdateIndExpenseInteractorTest {

  private final InMemorySessionState session = new InMemorySessionState();

  private final UUID payerId = UUID.randomUUID();
  private final UUID expenseId = UUID.randomUUID();
  private boolean changed;
  private String savedDescription;
  private double savedAmount;
  private final UpdateIndExpenseViewModel viewModel = new UpdateIndExpenseViewModel();
  private UpdateIndExpenseController controller;

  /** Signs in the payer before each request. */
  @BeforeEach
  void signInPayer() {
    session.setCurrentUserId(payerId);
    TestExpenseDataAccess dataAccess = new TestExpenseDataAccess();
    UpdateIndExpensePresenter presenter = new UpdateIndExpensePresenter(viewModel);
    UpdateIndExpenseInteractor interactor =
            new UpdateIndExpenseInteractor(dataAccess, session, presenter);
    controller = new UpdateIndExpenseController(interactor);
  }

  /** Tests that a valid expense request reaches the view. */
  @Test
  void validRequestUpdatesViewModel() {
    controller.execute(expenseId, " Groceries ", 10.10);

    assertTrue(changed);
    assertEquals("Groceries", savedDescription);
    assertEquals(10.10, savedAmount);
    assertEquals(expenseId, viewModel.getState().expenseId());
    assertEquals("Groceries", viewModel.getState().description());
    assertEquals(10.10, viewModel.getState().amount());
    assertNull(viewModel.getState().errorMessage());
  }

  /** Tests that another user's request clears an earlier success. */
  @Test
  void rejectsOtherRequester() {
    controller.execute(expenseId, " Groceries ", 10.10);
    session.setCurrentUserId(UUID.randomUUID());
    controller.execute(expenseId, "Tea", 3);

    assertFalse(changed);
    assertNull(viewModel.getState().expenseId());
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Tests that missing expenses are reported as failures. */
  @Test
  void rejectsUnknownExpense() {
    controller.execute(UUID.randomUUID(), "Tea", 3);
    assertNull(viewModel.getState().expenseId());
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Rejects zero. */
  @Test
  void rejectsZero() {
    controller.execute(expenseId, "Tea", 0);
    assertFalse(changed);
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Rejects a negative total. */
  @Test
  void rejectsNegative() {
    controller.execute(expenseId, "Tea", -1);
    assertFalse(changed);
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Rejects a fraction of a cent. */
  @Test
  void rejectsFractionalCent() {
    controller.execute(expenseId, "Tea", 0.001);
    assertFalse(changed);
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Rejects a price above the maximum. */
  @Test
  void rejectsAboveLimit() {
    controller.execute(expenseId, "Tea", 10_000_000_000.00);
    assertFalse(changed);
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Tests that an expense needs a purchase description. */
  @Test
  void rejectsBlankDescription() {
    controller.execute(expenseId, " ", 3);
    assertFalse(changed);
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Tests that a signed-out user cannot change expenses. */
  @Test
  void rejectsSignedOutUser() {
    session.clear();
    controller.execute(expenseId, "Tea", 3);
    assertFalse(changed);
    assertEquals("Sign in to manage your expenses.", viewModel.getState().errorMessage());
  }

  /** Stores the expense test results in memory. */
  private class TestExpenseDataAccess implements UpdateIndExpenseDataAccessInterface {
    @Override
    public boolean updateExpense(UUID id, UUID requester, String description, double amount) {
      changed = expenseId.equals(id) && payerId.equals(requester);
      if (changed) {
        savedDescription = description;
        savedAmount = amount;
      }
      return changed;
    }
  }
}
