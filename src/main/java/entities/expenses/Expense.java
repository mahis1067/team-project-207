package entities.expenses;

import java.time.LocalDateTime;
import java.util.ArrayList;
import entities.users.User;
import java.util.List;
import java.util.UUID;

/** Represents an expense between a certain number of users. */
public class Expense {
    private UUID expenseId;
    private String description;
    private double amount;
    private User createdBy;
    private User payer;
    private List<User> participants;
    private LocalDateTime dateCreated;
    private String statusAll;

    /**
     * Creates an expense.
     *
     * @param expenseId the unique identifier of this expense
     * @param description the description of what the purchase was
     * @param amount the total purchase amount
     * @param createdBy the user who entered the expense
     * @param payer the user who paid
     * @param participants the users included in the expense
     * @param dateCreated when the expense was created
     * @param statusAll the status of the expense (pending, paid, unpaid)
     */
    public Expense(
            UUID expenseId,
            String description,
            double amount,
            User createdBy,
            User payer,
            List<User> participants,
            LocalDateTime dateCreated,
            String statusAll) {

        this.expenseId = expenseId;
        this.description = description;
        this.amount = amount;
        this.createdBy = createdBy;
        this.payer = payer;
        this.participants = new ArrayList<>(participants);
        this.dateCreated = dateCreated;
        this.statusAll = statusAll;


    }

}
