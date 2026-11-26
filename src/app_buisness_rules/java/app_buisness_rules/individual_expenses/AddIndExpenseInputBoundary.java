package app_buisness_rules.individual_expenses;

/** Accepts requests for adding a one-to-one expense. */
public interface AddIndExpenseInputBoundary {

  /**
   * Executes the expense request.
   *
   * @param input the details of the request
   */
  void execute(AddIndExpenseInputData input);
}
