package interface_adapters.individual_expenses;

import app_buisness_rules.individual_expenses.AddIndExpenseInputBoundary;
import app_buisness_rules.individual_expenses.AddIndExpenseInputData;
import java.util.UUID;

/** Passes requests for adding a one-to-one expense to the use case. */
public class AddIndExpenseController {

  private final AddIndExpenseInputBoundary interactor;

  /**
   * Creates the expense controller.
   *
   * @param interactor the use case that handles the request
   */
  public AddIndExpenseController(AddIndExpenseInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Submits the expense request.
   *
   * @param otherParticipantId the other user sharing the expense
   * @param description what the purchase was for
   * @param amount the total purchase amount
   */
  public void execute(
          UUID otherParticipantId,
          String description,
          double amount) {
    interactor.execute(new AddIndExpenseInputData(
            otherParticipantId, description, amount));
  }
}
