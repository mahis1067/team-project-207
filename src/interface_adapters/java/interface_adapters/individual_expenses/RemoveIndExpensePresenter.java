package interface_adapters.individual_expenses;

import app_buisness_rules.individual_expenses.RemoveIndExpenseOutputBoundary;
import app_buisness_rules.individual_expenses.RemoveIndExpenseOutputData;

/** Updates the view after an expense request. */
public class RemoveIndExpensePresenter implements RemoveIndExpenseOutputBoundary {

  private final RemoveIndExpenseViewModel viewModel;

  /**
   * Creates the expense presenter.
   *
   * @param viewModel the view model that receives the result
   */
  public RemoveIndExpensePresenter(RemoveIndExpenseViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(RemoveIndExpenseOutputData output) {
    viewModel.setState(new RemoveIndExpenseViewModel.State(
            output.expenseId(), null));
  }

  @Override
  public void prepareFailView(String message) {
    viewModel.setState(new RemoveIndExpenseViewModel.State(null, message));
  }
}
