package superbase;

import app_buisness_rules.individual_expenses.AddIndExpenseDataAccessInterface;
import app_buisness_rules.individual_expenses.RemoveIndExpenseDataAccessInterface;
import app_buisness_rules.individual_expenses.UpdateIndExpenseDataAccessInterface;
import entities.expenses.Expense;
import entities.users.User;
import java.sql.SQLException;
import java.util.UUID;

/** Adapts the expense database to the three individual expense use cases. */
public class ExpenseDataAccess implements AddIndExpenseDataAccessInterface,
        RemoveIndExpenseDataAccessInterface, UpdateIndExpenseDataAccessInterface {

  private final SupabaseExpenseDao expenseDao;

  /**
   * Creates the database adapter.
   *
   * @param source the database connection provider
   */
  public ExpenseDataAccess(PostgresDataSource source) {
    expenseDao = new SupabaseExpenseDao(source);
  }

  @Override
  public User findUser(UUID userId) {
    try {
      return expenseDao.findUser(userId);
    } catch (SQLException exception) {
      throw new IllegalStateException("Unable to find the participant.", exception);
    }
  }

  @Override
  public void saveExpense(Expense expense) {
    try {
      expenseDao.insert(expense);
    } catch (SQLException exception) {
      throw new IllegalStateException("Unable to save the expense.", exception);
    }
  }

  @Override
  public boolean removeExpense(UUID expenseId, UUID requesterId) {
    try {
      return expenseDao.delete(expenseId, requesterId);
    } catch (SQLException exception) {
      throw new IllegalStateException("Unable to remove the expense.", exception);
    }
  }

  @Override
  public boolean updateExpense(
          UUID expenseId, UUID requesterId, String description, double amount) {
    try {
      return expenseDao.update(expenseId, requesterId, description, amount);
    } catch (SQLException exception) {
      throw new IllegalStateException("Unable to update the expense.", exception);
    }
  }
}
