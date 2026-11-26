package app_buisness_rules.individual_expenses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import entities.expenses.Expense;
import entities.users.User;
import interface_adapters.individual_expenses.AddIndExpenseController;
import interface_adapters.individual_expenses.AddIndExpensePresenter;
import interface_adapters.individual_expenses.AddIndExpenseViewModel;
import interface_adapters.session.InMemorySessionState;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests the add expense use case through its controller and presenter. */
class AddIndExpenseInteractorTest {

  private final InMemorySessionState session = new InMemorySessionState();

  private final User payer =
          new User(UUID.randomUUID(), "payer", "hash", "Test", "Payer", new ArrayList<>());
  private final User other =
          new User(UUID.randomUUID(), "other", "hash", "Test", "Other", new ArrayList<>());
  private Expense savedExpense;

  private final AddIndExpenseViewModel viewModel = new AddIndExpenseViewModel();
  private AddIndExpenseController controller;

  /** Signs in the payer before each request. */
  @BeforeEach
  void signInPayer() {
    session.setCurrentUserId(payer.getUserId());
    TestExpenseDataAccess dataAccess = new TestExpenseDataAccess();
    AddIndExpensePresenter presenter = new AddIndExpensePresenter(viewModel);
    AddIndExpenseInteractor interactor =
            new AddIndExpenseInteractor(dataAccess, session, presenter);
    controller = new AddIndExpenseController(interactor);
  }

  /** Tests valid expense request goe the view. */
  @Test
  void validRequestUpdatesViewModel() {
    controller.execute(other.getUserId(), " Groceries ", 10.10);

    assertEquals(2, savedExpense.getParticipants().size());
    assertEquals(payer, savedExpense.getParticipants().get(0));
    assertEquals(other, savedExpense.getParticipants().get(1));
    assertEquals(payer, savedExpense.getPayer());
    assertEquals(payer, savedExpense.getCreatedBy());
    assertEquals("pending", savedExpense.getStatusAll());
    assertNotNull(savedExpense.getDateCreated());
    assertEquals(savedExpense.getExpenseId(), viewModel.getState().expenseId());
    assertEquals("Groceries", viewModel.getState().description());
    assertEquals(10.10, viewModel.getState().amount());
    assertNull(viewModel.getState().errorMessage());
  }

  /** Tests that the same user cannot occupy both places in an expense. */
  @Test
  void rejectsRepeatedParticipant() {
    controller.execute(payer.getUserId(), "Tea", 3);
    assertNull(savedExpense);
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Tests that both participants must have accounts. */
  @Test
  void rejectsUnknownAccounts() {
    session.setCurrentUserId(UUID.randomUUID());
    controller.execute(other.getUserId(), "Tea", 3);
    assertNull(savedExpense);
    assertNotNull(viewModel.getState().errorMessage());

    session.setCurrentUserId(payer.getUserId());
    controller.execute(UUID.randomUUID(), "Tea", 3);
    assertNull(savedExpense);
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Rejects zero. */
  @Test
  void rejectsZero() {
    controller.execute(other.getUserId(), "Tea", 0);
    assertNull(savedExpense);
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Rejects a negative total. */
  @Test
  void rejectsNegative() {
    controller.execute(other.getUserId(), "Tea", -1);
    assertNull(savedExpense);
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Rejects a fraction of a cent. */
  @Test
  void rejectsFractionalCent() {
    controller.execute(other.getUserId(), "Tea", 0.001);
    assertNull(savedExpense);
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Rejects a price above the maximum. */
  @Test
  void rejectsAboveLimit() {
    controller.execute(other.getUserId(), "Tea", 10_000_000_000.00);
    assertNull(savedExpense);
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Tests that an expense needs a purchase description. */
  @Test
  void rejectsBlankDescription() {
    controller.execute(other.getUserId(), " ", 3);
    assertNull(savedExpense);
    assertNotNull(viewModel.getState().errorMessage());
  }

  /** Tests that a signed-out user cannot change expenses. */
  @Test
  void rejectsSignedOutUser() {
    session.clear();
    controller.execute(other.getUserId(), "Tea", 3);
    assertNull(savedExpense);
    assertEquals("Sign in to manage your expenses.", viewModel.getState().errorMessage());
  }

  /** Stores the expense test results in memory. */
  private class TestExpenseDataAccess implements AddIndExpenseDataAccessInterface {
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
      savedExpense = expense;
    }
  }
}
