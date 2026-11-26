package superbase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import app_buisness_rules.individual_expenses.AddIndExpenseInteractor;
import entities.users.User;
import interface_adapters.individual_expenses.AddIndExpenseController;
import interface_adapters.individual_expenses.AddIndExpensePresenter;
import interface_adapters.individual_expenses.AddIndExpenseViewModel;
import interface_adapters.session.InMemorySessionState;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/** Tests expense persistence without using the application's database configuration. */
class ExpenseDataAccessTest {

  /** Tests that persistence errors reach the view without exposing connection details. */
  @Test
  void connectionFailureReachesPresenter() {
    PostgresDataSource unavailable = new UnavailableDataSource();
    ExpenseDataAccess dataAccess = new ExpenseDataAccess(unavailable);
    AddIndExpenseViewModel viewModel = new AddIndExpenseViewModel();
    UUID payerId = UUID.randomUUID();
    InMemorySessionState session = new InMemorySessionState();
    session.setCurrentUserId(payerId);
    AddIndExpensePresenter presenter = new AddIndExpensePresenter(viewModel);
    AddIndExpenseInteractor interactor =
            new AddIndExpenseInteractor(dataAccess, session, presenter);
    AddIndExpenseController controller = new AddIndExpenseController(interactor);
    controller.execute(UUID.randomUUID(), "Tea", 3);

    assertNull(viewModel.getState().expenseId());
    assertEquals("Unable to save the expense.", viewModel.getState().errorMessage());
    try {
      dataAccess.removeExpense(UUID.randomUUID(), payerId);
      fail("Removing an expense should fail when the connection is unavailable.");
    } catch (IllegalStateException exception) {
      assertEquals("Unable to remove the expense.", exception.getMessage());
    }

    try {
      dataAccess.updateExpense(UUID.randomUUID(), payerId, "Tea", 3);
      fail("Updating an expense should fail when the connection is unavailable.");
    } catch (IllegalStateException exception) {
      assertEquals("Unable to update the expense.", exception.getMessage());
    }
  }

  /**
   * Tests creation, ownership checks, updates and removal using a private in-memory fixture.
   * Add H2 to the test classpath to run this case.
   *
   * @throws SQLException if the JDBC operations fail
   */
  @Test
  void payerCanChangeAndRemoveSharedExpense() throws SQLException {
    try {
      Class.forName("org.h2.Driver");
    } catch (ClassNotFoundException exception) {
      Assumptions.abort("H2 is required for the in-memory SQL fixture.");
    }
    PostgresDataSource source = new TestDataSource();
    UUID payerId = UUID.randomUUID();
    UUID other = UUID.randomUUID();
    try (Connection fixture = source.getConnection();
         Statement setup = fixture.createStatement()) {
      setup.execute("create table users (id uuid primary key, username text, "
              + "first_name text, last_name text)");
      setup.execute("create table expenses (id uuid primary key, description text not null, "
              + "amount numeric(12,2) not null, created_at timestamp with time zone not null, "
              + "paid_by uuid references users, "
              + "other_participant_id uuid references users, status text not null)");
      insertUser(fixture, payerId, "payer");
      insertUser(fixture, other, "other");

      ExpenseDataAccess dataAccess = new ExpenseDataAccess(source);
      User payer = dataAccess.findUser(payerId);
      assertEquals("payer", payer.getUsername());
      assertNull(dataAccess.findUser(UUID.randomUUID()));
      AddIndExpenseViewModel viewModel = new AddIndExpenseViewModel();
      InMemorySessionState session = new InMemorySessionState();
      session.setCurrentUserId(payerId);
      AddIndExpensePresenter presenter = new AddIndExpensePresenter(viewModel);
      AddIndExpenseInteractor interactor =
            new AddIndExpenseInteractor(dataAccess, session, presenter);
      AddIndExpenseController controller = new AddIndExpenseController(interactor);
      controller.execute(other, "Tea", 3.10);
      UUID expenseId = viewModel.getState().expenseId();
      assertNotNull(expenseId, viewModel.getState().errorMessage());

      assertFalse(dataAccess.updateExpense(expenseId, other, "Lunch", 4.15));
      assertFalse(dataAccess.removeExpense(expenseId, other));
      assertTrue(dataAccess.updateExpense(expenseId, payerId, "Lunch", 4.15));
      try (ResultSet result = setup.executeQuery("select * from expenses")) {
        assertTrue(result.next());
        assertEquals(expenseId, result.getObject("id", UUID.class));
        assertEquals(other, result.getObject("other_participant_id", UUID.class));
        assertEquals(payerId, result.getObject("paid_by", UUID.class));
        assertEquals("pending", result.getString("status"));
        assertEquals("Lunch", result.getString("description"));
        assertEquals("4.15", result.getBigDecimal("amount").toPlainString());
        assertNotNull(result.getObject("created_at"));
        assertFalse(result.next());
      }
      assertTrue(dataAccess.removeExpense(expenseId, payerId));
      assertFalse(dataAccess.removeExpense(expenseId, payerId));
      assertFalse(dataAccess.updateExpense(expenseId, payerId, "Tea", 3.10));
    }
  }

  private void insertUser(Connection connection, UUID userId, String username) throws SQLException {
    String sql = "insert into users values (?, ?, 'Test', 'User')";
    try (PreparedStatement insert = connection.prepareStatement(sql)) {
      insert.setObject(1, userId);
      insert.setString(2, username);
      insert.executeUpdate();
    }
  }

  /** Simulates a connection failure. */
  private static class UnavailableDataSource extends PostgresDataSource {
    @Override
    public Connection getConnection() throws SQLException {
      throw new SQLException("Test connection unavailable.");
    }
  }

  /** Opens connections to the same private database for one test. */
  private static class TestDataSource extends PostgresDataSource {
    private final String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=PostgreSQL";

    @Override
    public Connection getConnection() throws SQLException {
      return DriverManager.getConnection(url);
    }
  }
}
