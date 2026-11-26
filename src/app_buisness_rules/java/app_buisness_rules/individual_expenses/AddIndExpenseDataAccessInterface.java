package app_buisness_rules.individual_expenses;

import entities.expenses.Expense;
import entities.users.User;
import java.util.UUID;

/** Provides the stored information needed for adding a one-to-one expense. */
public interface AddIndExpenseDataAccessInterface {

  /**
   * Looks up an expense participant.
   *
   * @param userId the unique identifier of the user
   * @return the user, or null if the account does not exist
   */
  User findUser(UUID userId);

  /**
   * Saves the one-to-one expense.
   *
   * @param expense the expense to save
   */
  void saveExpense(Expense expense);
}
