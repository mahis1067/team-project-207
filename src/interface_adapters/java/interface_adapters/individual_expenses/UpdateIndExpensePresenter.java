package interface_adapters.individual_expenses;

import app_buisness_rules.individual_expenses.UpdateIndExpenseOutputBoundary;
import app_buisness_rules.individual_expenses.UpdateIndExpenseOutputData;

/** Updates the view after an expense request. */
public class UpdateIndExpensePresenter implements UpdateIndExpenseOutputBoundary {

  private final UpdateIndExpenseViewModel viewModel;

  /**
   * Creates the expense presenter.
   *
   * @param viewModel the view model that receives the result
   */
  public UpdateIndExpensePresenter(UpdateIndExpenseViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(UpdateIndExpenseOutputData output) {
    viewModel.setState(new UpdateIndExpenseViewModel.State(
            output.expenseId(), output.description(), output.amount(), null));
  }

  @Override
  public void prepareFailView(String message) {
    viewModel.setState(new UpdateIndExpenseViewModel.State(null, null, 0, message));
  }
}
