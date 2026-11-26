package app_buisness_rules.individual_expenses;

import java.util.UUID;

/**
 * Result of adding a one-to-one expense.
 *
 * @param expenseId the unique identifier of the expense
 * @param description what the purchase was for
 * @param amount the total purchase amount
 */
public record AddIndExpenseOutputData(
        UUID expenseId,
        String description,
        double amount) {
}
