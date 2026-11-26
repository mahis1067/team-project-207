package app_buisness_rules.individual_expenses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import interface_adapters.individual_expenses.RemoveIndExpenseController;
import interface_adapters.individual_expenses.RemoveIndExpensePresenter;
import interface_adapters.individual_expenses.RemoveIndExpenseViewModel;
import interface_adapters.session.InMemorySessionState;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests the remove expense use case through its controller and presenter. */
class RemoveIndExpenseInteractorTest {

  private final InMemorySessionState session = new InMemorySessionState();

  private final UUID payerId = UUID.randomUUID();
  private final UUID expenseId = UUID.randomUUID();
  private boolean changed;
  private final RemoveIndExpenseViewModel viewModel = new RemoveIndExpenseViewModel();
  private RemoveIndExpenseController controller;

  /** Signs in the payer before each request. */
  @BeforeEach
  void signInPayer() {
    session.setCurrentUserId(payerId);
    TestExpenseDataAccess dataAccess = new TestExpenseDataAccess();
    RemoveIndExpensePresenter presenter = new RemoveIndExpensePresenter(viewModel);
    RemoveIndExpenseInteractor interactor =
            new RemoveIndExpenseInteractor(dataAccess, session, presenter);
    controller = new RemoveIndExpenseController(interactor);
  }

  /** Tests that a valid expense request reaches the view. */
  @Test
  void validRequestUpdatesViewModel() {
    controller.execute(expenseId);

    assertTrue(changed);
    assertEquals(expenseId, viewModel.getState().expenseId());
    assertNull(viewModel.getState().errorMessage());
  }

  /** Tests that another user's request clears an earlier success. */
  @Test
  void rejectsOtherRequester() {
    controller.execute(expenseId);
    session.setCurrentUserId(UUID.randomUUID());
    controller.execute(expenseId);

    assertFalse(changed);
    assertNull(viewModel.getState().expenseId());
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Tests that missing expenses are reported as failures. */
  @Test
  void rejectsUnknownExpense() {
    controller.execute(UUID.randomUUID());
    assertNull(viewModel.getState().expenseId());
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Tests that a signed-out user cannot change expenses. */
  @Test
  void rejectsSignedOutUser() {
    session.clear();
    controller.execute(expenseId);
    assertFalse(changed);
    assertEquals("Sign in to manage your expenses.", viewModel.getState().errorMessage());
  }

  /** Stores the expense test results in memory. */
  private class TestExpenseDataAccess implements RemoveIndExpenseDataAccessInterface {
    @Override
    public boolean removeExpense(UUID id, UUID requester) {
      changed = expenseId.equals(id) && payerId.equals(requester);
      return changed;
    }
  }
}
