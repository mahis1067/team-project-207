package app_buisness_rules.individual_expenses;

import java.util.UUID;

/**
 * Information needed for adding a one-to-one expense.
 *
 * @param otherParticipantId the other user sharing the expense
 * @param description what the purchase was for
 * @param amount the total purchase amount
 */
public record AddIndExpenseInputData(
        UUID otherParticipantId,
        String description,
        double amount) {
}
