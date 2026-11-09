package superbase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Creates PostgreSQL connections from the runtime Supabase configuration.
 */
public class PostgresDataSource {

    /**
     * Opens a database connection using the three required environment variables.
     *
     * @return an open PostgreSQL connection
     * @throws SQLException if configuration is missing or the connection cannot be opened
     */
    public Connection getConnection() throws SQLException {
        String url = requiredEnvironment("SUPABASE_DB_URL");
        String user = requiredEnvironment("SUPABASE_DB_USER");
        String password = requiredEnvironment("SUPABASE_DB_PASSWORD");
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Reads a required environment variable.
     *
     * @param name the environment variable name
     * @return the configured, non-blank value
     * @throws SQLException if the variable is missing or blank
     */
    private String requiredEnvironment(String name) throws SQLException {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new SQLException("Missing required environment variable: " + name);
        }
        return value;
    }
}
