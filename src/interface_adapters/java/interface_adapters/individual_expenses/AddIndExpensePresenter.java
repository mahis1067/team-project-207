package interface_adapters.individual_expenses;

import app_buisness_rules.individual_expenses.AddIndExpenseOutputBoundary;
import app_buisness_rules.individual_expenses.AddIndExpenseOutputData;

/** Updates the view after an expense request. */
public class AddIndExpensePresenter implements AddIndExpenseOutputBoundary {

  private final AddIndExpenseViewModel viewModel;

  /**
   * Creates the expense presenter.
   *
   * @param viewModel the view model that receives the result
   */
  public AddIndExpensePresenter(AddIndExpenseViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(AddIndExpenseOutputData output) {
    viewModel.setState(new AddIndExpenseViewModel.State(
            output.expenseId(), output.description(), output.amount(), null));
  }

  @Override
  public void prepareFailView(String message) {
    viewModel.setState(new AddIndExpenseViewModel.State(null, null, 0, message));
  }
}
