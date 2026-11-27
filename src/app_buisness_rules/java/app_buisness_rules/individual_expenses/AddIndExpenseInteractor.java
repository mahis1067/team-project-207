package app_buisness_rules.individual_expenses;

import app_buisness_rules.notifications.NotificationEventGateway;
import app_business_rules.logout.SessionState;
import entities.expenses.Expense;
import entities.users.User;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Handles adding a one-to-one expense. */
public class AddIndExpenseInteractor implements AddIndExpenseInputBoundary {

  private static final double MAX_AMOUNT = 9_999_999_999.99;

  private final SessionState session;
  private final AddIndExpenseDataAccessInterface dataAccess;
  private final AddIndExpenseOutputBoundary presenter;
  private final NotificationEventGateway notifications;

  /**
   * Creates the expense interactor.
   *
   * @param dataAccess access to stored expense information
   * @param session the current login session
   * @param presenter receives the result of the request
   */
  public AddIndExpenseInteractor(
          AddIndExpenseDataAccessInterface dataAccess,
          SessionState session,
          AddIndExpenseOutputBoundary presenter) {
    this(dataAccess, session, presenter, null);
  }

  /**
   * Creates the expense interactor with notification publishing.
   *
   * @param dataAccess access to stored expense information
   * @param session the current login session
   * @param presenter receives the result of the request
   * @param notifications notification event gateway
   */
  public AddIndExpenseInteractor(
          AddIndExpenseDataAccessInterface dataAccess,
          SessionState session,
          AddIndExpenseOutputBoundary presenter,
          NotificationEventGateway notifications) {
    this.dataAccess = dataAccess;
    this.session = session;
    this.presenter = presenter;
    this.notifications = notifications;
  }

  @Override
  public void execute(AddIndExpenseInputData input) {
    UUID payerId = session.getCurrentUserId();
    if (payerId == null) {
      presenter.prepareFailView("Sign in to manage your expenses.");
      return;
    }
    if (input == null || input.otherParticipantId() == null) {
      presenter.prepareFailView("Choose the other participant.");
      return;
    }
    if (payerId.equals(input.otherParticipantId())) {
      presenter.prepareFailView("Choose two different participants.");
      return;
    }
    if (input.description() == null || input.description().isBlank()) {
      presenter.prepareFailView("Enter a description.");
      return;
    }
    double amount = input.amount();
    boolean withinRange = amount > 0 && amount <= MAX_AMOUNT;
    // Rounding to the nearest cent must leave the original price unchanged.
    double roundedAmount = Math.round(amount * 100) / 100.0;
    if (!withinRange || amount != roundedAmount) {
      presenter.prepareFailView("Enter a positive amount within the limit, in whole cents.");
      return;
    }
    String description = input.description().strip();
    Expense expense;
    try {
      User payer = dataAccess.findUser(payerId);
      User otherParticipant = dataAccess.findUser(input.otherParticipantId());
      if (payer == null || otherParticipant == null) {
        presenter.prepareFailView("Both participants must have an account.");
        return;
      }
      List<User> participants = new ArrayList<>();
      participants.add(payer);
      participants.add(otherParticipant);
      expense = new Expense(
              UUID.randomUUID(), description, amount, payer, payer,
              participants, LocalDateTime.now(ZoneOffset.UTC), "pending");
      dataAccess.saveExpense(expense);
    } catch (IllegalStateException exception) {
      presenter.prepareFailView("Unable to save the expense.");
      return;
    }
    if (notifications != null) {
      try {
        notifications.expenseAdded(expense);
      } catch (RuntimeException notificationFailure) {
        // Expense was saved; notification delivery is best-effort.
        java.util.logging.Logger.getLogger(
            AddIndExpenseInteractor.class.getName())
            .log(java.util.logging.Level.WARNING,
                "Expense saved, but notification publishing failed.",
                notificationFailure);
      }
    }
    presenter.prepareSuccessView(
            new AddIndExpenseOutputData(expense.getExpenseId(), description, amount));
  }
}
