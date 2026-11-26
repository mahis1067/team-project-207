package app_buisness_rules.individual_expenses;

/** Accepts requests for updating a one-to-one expense. */
public interface UpdateIndExpenseInputBoundary {

  /**
   * Executes the expense request.
   *
   * @param input the details of the request
   */
  void execute(UpdateIndExpenseInputData input);
}
