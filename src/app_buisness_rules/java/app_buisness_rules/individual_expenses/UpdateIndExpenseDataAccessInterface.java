package app_buisness_rules.individual_expenses;

import java.util.UUID;

/** Provides the stored information needed for updating a one-to-one expense. */
public interface UpdateIndExpenseDataAccessInterface {

  /**
   * Updates the expense if it belongs to the requester.
   *
   * @param expenseId the unique identifier of the expense
   * @param requesterId the user requesting the change
   * @param description what the purchase was for
   * @param amount the total purchase amount
   * @return true if the signed-in payer's expense was updated
   */
  boolean updateExpense(UUID expenseId, UUID requesterId, String description, double amount);
}
