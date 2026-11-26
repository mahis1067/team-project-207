package app_buisness_rules.individual_expenses;

import java.util.UUID;

/** Provides the stored information needed for removing a one-to-one expense. */
public interface RemoveIndExpenseDataAccessInterface {

  /**
   * Removes the expense if it belongs to the requester.
   *
   * @param expenseId the unique identifier of the expense
   * @param requesterId the user requesting the change
   * @return true if the signed-in payer's expense was removed
   */
  boolean removeExpense(UUID expenseId, UUID requesterId);
}
