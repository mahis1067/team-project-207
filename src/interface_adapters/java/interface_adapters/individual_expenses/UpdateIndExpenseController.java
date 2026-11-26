package interface_adapters.individual_expenses;

import app_buisness_rules.individual_expenses.UpdateIndExpenseInputBoundary;
import app_buisness_rules.individual_expenses.UpdateIndExpenseInputData;
import java.util.UUID;

/** Passes requests for updating a one-to-one expense to the use case. */
public class UpdateIndExpenseController {

  private final UpdateIndExpenseInputBoundary interactor;

  /**
   * Creates the expense controller.
   *
   * @param interactor the use case that handles the request
   */
  public UpdateIndExpenseController(UpdateIndExpenseInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Submits the expense request.
   *
   * @param expenseId the unique identifier of the expense
   * @param description what the purchase was for
   * @param amount the total purchase amount
   */
  public void execute(
          UUID expenseId,
          String description,
          double amount) {
    interactor.execute(new UpdateIndExpenseInputData(
            expenseId, description, amount));
  }
}
