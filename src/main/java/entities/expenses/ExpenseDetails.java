package entities.expenses;

import entities.users.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Holds the fields required to create an expense.
 *
 * @param expenseId the expense's unique identifier
 * @param description the description of the purchase
 * @param amount the total purchase amount
 * @param createdBy the user who entered the expense
 * @param payer the user who paid
 * @param participants the users included in the expense
 * @param dateCreated when the expense was created
 * @param statusAll the status of the expense
 */
public record ExpenseDetails(
    UUID expenseId,
    String description,
    double amount,
    User createdBy,
    User payer,
    List<User> participants,
    LocalDateTime dateCreated,
    String statusAll) {
}
