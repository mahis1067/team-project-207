package app_buisness_rules.individual_expenses;

import app_business_rules.logout.SessionState;
import java.util.UUID;

/** Handles removing a one-to-one expense. */
public class RemoveIndExpenseInteractor implements RemoveIndExpenseInputBoundary {

  private final SessionState session;
  private final RemoveIndExpenseDataAccessInterface dataAccess;
  private final RemoveIndExpenseOutputBoundary presenter;

  /**
   * Creates the expense interactor.
   *
   * @param dataAccess access to stored expense information
   * @param session the current login session
   * @param presenter receives the result of the request
   */
  public RemoveIndExpenseInteractor(
          RemoveIndExpenseDataAccessInterface dataAccess,
          SessionState session,
          RemoveIndExpenseOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.session = session;
    this.presenter = presenter;
  }

  @Override
  public void execute(RemoveIndExpenseInputData input) {
    UUID payerId = session.getCurrentUserId();
    if (payerId == null) {
      presenter.prepareFailView("Sign in to manage your expenses.");
      return;
    }
    if (input == null || input.expenseId() == null) {
      presenter.prepareFailView("An expense ID is required.");
      return;
    }
    boolean removed;
    try {
      removed = dataAccess.removeExpense(input.expenseId(), payerId);
    } catch (IllegalStateException exception) {
      presenter.prepareFailView("Unable to remove the expense.");
      return;
    }
    if (!removed) {
      presenter.prepareFailView("Expense not found or you do not own it.");
      return;
    }
    presenter.prepareSuccessView(new RemoveIndExpenseOutputData(
            input.expenseId()));
  }
}
