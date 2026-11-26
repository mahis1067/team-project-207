package app_buisness_rules.individual_expenses;

/** Presents the result of removing a one-to-one expense. */
public interface RemoveIndExpenseOutputBoundary {

  /**
   * Presents the saved result.
   *
   * @param output the removed expense details
   */
  void prepareSuccessView(RemoveIndExpenseOutputData output);

  /**
   * Presents the failure message.
   *
   * @param message the reason the request failed
   */
  void prepareFailView(String message);
}
