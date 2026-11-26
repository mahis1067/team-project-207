package app_buisness_rules.individual_expenses;

import java.util.UUID;

/**
 * Result of removing a one-to-one expense.
 *
 * @param expenseId the unique identifier of the expense
 */
public record RemoveIndExpenseOutputData(
        UUID expenseId) {
}
