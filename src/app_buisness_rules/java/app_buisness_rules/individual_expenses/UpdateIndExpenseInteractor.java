package app_buisness_rules.individual_expenses;

import app_business_rules.logout.SessionState;
import java.util.UUID;

/** Handles updating a one-to-one expense. */
public class UpdateIndExpenseInteractor implements UpdateIndExpenseInputBoundary {

  private static final double MAX_AMOUNT = 9_999_999_999.99;

  private final SessionState session;
  private final UpdateIndExpenseDataAccessInterface dataAccess;
  private final UpdateIndExpenseOutputBoundary presenter;

  /**
   * Creates the expense interactor.
   *
   * @param dataAccess access to stored expense information
   * @param session the current login session
   * @param presenter receives the result of the request
   */
  public UpdateIndExpenseInteractor(
          UpdateIndExpenseDataAccessInterface dataAccess,
          SessionState session,
          UpdateIndExpenseOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.session = session;
    this.presenter = presenter;
  }

  @Override
  public void execute(UpdateIndExpenseInputData input) {
    UUID payerId = session.getCurrentUserId();
    if (payerId == null) {
      presenter.prepareFailView("Sign in to manage your expenses.");
      return;
    }
    if (input == null || input.expenseId() == null) {
      presenter.prepareFailView("An expense ID is required.");
      return;
    }
    if (input.description() == null || input.description().isBlank()) {
      presenter.prepareFailView("Enter a description.");
      return;
    }
    double amount = input.amount();
    boolean withinRange = amount > 0 && amount <= MAX_AMOUNT;
    // Rounding to the nearest cent must leave the original price unchanged.
    double roundedAmount = Math.round(amount * 100) / 100.0;
    if (!withinRange || amount != roundedAmount) {
      presenter.prepareFailView("Enter a positive amount within the limit, in whole cents.");
      return;
    }
    String description = input.description().strip();
    boolean updated;
    try {
      updated = dataAccess.updateExpense(
              input.expenseId(), payerId, description, amount);
    } catch (IllegalStateException exception) {
      presenter.prepareFailView("Unable to update the expense.");
      return;
    }
    if (!updated) {
      presenter.prepareFailView("Expense not found or you do not own it.");
      return;
    }
    presenter.prepareSuccessView(new UpdateIndExpenseOutputData(
            input.expenseId(), description, amount));
  }
}
