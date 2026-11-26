package app_buisness_rules.individual_expenses;

/** Presents the result of updating a one-to-one expense. */
public interface UpdateIndExpenseOutputBoundary {

  /**
   * Presents the saved result.
   *
   * @param output the updated expense details
   */
  void prepareSuccessView(UpdateIndExpenseOutputData output);

  /**
   * Presents the failure message.
   *
   * @param message the reason the request failed
   */
  void prepareFailView(String message);
}
