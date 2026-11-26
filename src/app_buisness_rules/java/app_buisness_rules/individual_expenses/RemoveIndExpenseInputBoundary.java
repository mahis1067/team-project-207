package app_buisness_rules.individual_expenses;

/** Accepts requests for removing a one-to-one expense. */
public interface RemoveIndExpenseInputBoundary {

  /**
   * Executes the expense request.
   *
   * @param input the details of the request
   */
  void execute(RemoveIndExpenseInputData input);
}
