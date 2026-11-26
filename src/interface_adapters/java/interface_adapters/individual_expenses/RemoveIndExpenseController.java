package interface_adapters.individual_expenses;

import app_buisness_rules.individual_expenses.RemoveIndExpenseInputBoundary;
import app_buisness_rules.individual_expenses.RemoveIndExpenseInputData;
import java.util.UUID;

/** Passes requests for removing a one-to-one expense to the use case. */
public class RemoveIndExpenseController {

  private final RemoveIndExpenseInputBoundary interactor;

  /**
   * Creates the expense controller.
   *
   * @param interactor the use case that handles the request
   */
  public RemoveIndExpenseController(RemoveIndExpenseInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Submits the expense request.
   *
   * @param expenseId the unique identifier of the expense
   */
  public void execute(UUID expenseId) {
    interactor.execute(new RemoveIndExpenseInputData(expenseId));
  }
}
