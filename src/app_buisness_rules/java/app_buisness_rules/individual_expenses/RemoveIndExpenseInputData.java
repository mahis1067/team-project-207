package app_buisness_rules.individual_expenses;

import java.util.UUID;

/**
 * Information needed for removing a one-to-one expense.
 *
 * @param expenseId the unique identifier of the expense
 */
public record RemoveIndExpenseInputData(UUID expenseId) {
}
