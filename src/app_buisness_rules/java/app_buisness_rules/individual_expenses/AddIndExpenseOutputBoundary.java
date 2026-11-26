package app_buisness_rules.individual_expenses;

/** Presents the result of adding a one-to-one expense. */
public interface AddIndExpenseOutputBoundary {

  /**
   * Presents the saved result.
   *
   * @param output the added expense details
   */
  void prepareSuccessView(AddIndExpenseOutputData output);

  /**
   * Presents the failure message.
   *
   * @param message the reason the request failed
   */
  void prepareFailView(String message);
}
