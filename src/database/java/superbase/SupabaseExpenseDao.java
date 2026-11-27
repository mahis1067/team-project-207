package superbase;

import entities.expenses.Expense;
import entities.users.User;
import entities.users.UserFactory;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Reads and writes one-to-one expenses. The expense table must include
 * other_participant_id (user ID) and status (text) in addition to paid_by.
 * The payer and the other participant together form the pair.
 */
public class SupabaseExpenseDao {

  private final PostgresDataSource source;

  /**
   * Creates the database adapter.
   *
   * @param source the database connection provider
   */
  public SupabaseExpenseDao(PostgresDataSource source) {
    this.source = source;
  }

  /**
   * Loads the participant's public profile for the expense.
   *
   * @param userId the user to find
   * @return the user, or null if the account does not exist
   * @throws SQLException if the lookup fails
   */
  public User findUser(UUID userId) throws SQLException {
    String sql = "select username, first_name, last_name from users where id = ?";
    try (Connection connection = source.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setObject(1, userId);
      try (ResultSet result = statement.executeQuery()) {
        if (!result.next()) {
          return null;
        }
        return UserFactory.create(userId, result.getString("username"), null,
                result.getString("first_name"), result.getString("last_name"), new ArrayList<>());
      }
    }
  }

  /**
   * Saves the one-to-one expense.
   *
   * @param expense the expense shared by the payer and one other participant
   * @throws SQLException if the expense cannot be saved
   */
  public void insert(Expense expense) throws SQLException {
    List<User> participants = expense.getParticipants();
    UUID payerId = expense.getPayer().getUserId();
    if (!payerId.equals(expense.getCreatedBy().getUserId())) {
      throw new IllegalArgumentException("Only the payer can record an expense.");
    }
    if (participants.size() != 2) {
      throw new IllegalArgumentException("Expected two expense participants.");
    }
    UUID firstId = participants.get(0).getUserId();
    UUID secondId = participants.get(1).getUserId();
    if (firstId.equals(secondId)) {
      throw new IllegalArgumentException("Expected the payer and one other participant.");
    }

    UUID otherId;
    if (payerId.equals(firstId)) {
      otherId = secondId;
    } else if (payerId.equals(secondId)) {
      otherId = firstId;
    } else {
      throw new IllegalArgumentException("Expected the payer and one other participant.");
    }
    String sql = "insert into expenses "
            + "(id, description, amount, paid_by, "
            + "other_participant_id, created_at, status) "
            + "values (?, ?, ?, ?, ?, ?, ?)";
    try (Connection connection = source.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setObject(1, expense.getExpenseId());
      statement.setString(2, expense.getDescription());
      statement.setBigDecimal(3, BigDecimal.valueOf(expense.getAmount()));
      statement.setObject(4, payerId);
      statement.setObject(5, otherId);
      statement.setObject(6, expense.getDateCreated().atOffset(ZoneOffset.UTC));
      statement.setString(7, expense.getStatusAll());
      if (statement.executeUpdate() != 1) {
        throw new SQLException("The expense was not inserted.");
      }
    }
  }

  /**
   * Updates the expense if it belongs to the requester.
   *
   * @param expenseId the expense to update
   * @param requesterId the payer requesting the update
   * @param description the new purchase description
   * @param amount the new total
   * @return true if the payer's expense was updated
   * @throws SQLException if the update fails
   */
  public boolean update(UUID expenseId, UUID requesterId, String description, double amount)
          throws SQLException {
    String sql = "update expenses set description = ?, amount = ? "
            + "where id = ? and paid_by = ? and other_participant_id is not null";
    try (Connection connection = source.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, description);
      statement.setBigDecimal(2, BigDecimal.valueOf(amount));
      statement.setObject(3, expenseId);
      statement.setObject(4, requesterId);
      return statement.executeUpdate() == 1;
    }
  }

  /**
   * Removes the expense if it belongs to the requester.
   *
   * @param expenseId the expense to remove
   * @param requesterId the payer requesting removal
   * @return true if the payer's expense was removed
   * @throws SQLException if the removal fails
   */
  public boolean delete(UUID expenseId, UUID requesterId) throws SQLException {
    String sql = "delete from expenses where id = ? and paid_by = ? "
            + "and other_participant_id is not null";
    try (Connection connection = source.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setObject(1, expenseId);
      statement.setObject(2, requesterId);
      return statement.executeUpdate() == 1;
    }
  }
}
