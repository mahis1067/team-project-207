package entities.expenses;

/** Creates expense entities from persisted or application-provided data. */
public final class ExpenseFactory {

  private ExpenseFactory() {
  }

  /**
   * Creates an expense from its details.
   *
   * @param details the expense fields
   * @return the constructed expense
   */
  public static Expense create(final ExpenseDetails details) {
    return new Expense(
        details.expenseId(),
        details.description(),
        details.amount(),
        details.createdBy(),
        details.payer(),
        details.participants(),
        details.dateCreated(),
        details.statusAll());
  }
}
