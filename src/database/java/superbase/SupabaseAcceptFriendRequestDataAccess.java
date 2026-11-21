package superbase;

import app_buisness_rules.friend_requests.AcceptFriendRequestDataAccessInterface;
import java.sql.SQLException;
import java.util.UUID;

/** Supabase data-access implementation for accepting friend requests. */
public class SupabaseAcceptFriendRequestDataAccess
    implements AcceptFriendRequestDataAccessInterface {
  private final SupabaseFriendshipDao friendshipDao;

  /**
   * Creates the data-access implementation.
   *
   * @param source database connection provider
   */
  public SupabaseAcceptFriendRequestDataAccess(PostgresDataSource source) {
    friendshipDao = new SupabaseFriendshipDao(source);
  }

  @Override
  public void updateFriendship(UUID friendshipId) {
    try {
      friendshipDao.accept(friendshipId);
    } catch (SQLException exception) {
      throw new FriendRequestDatabaseException(
          "Unable to accept friend request.", exception);
    }
  }

  /** Wraps database failures with a use-case-level runtime exception. */
  public static class FriendRequestDatabaseException extends RuntimeException {
    /**
     * Creates the exception.
     *
     * @param message failure message
     * @param cause database error
     */
    public FriendRequestDatabaseException(String message, SQLException cause) {
      super(message, cause);
    }
  }
}
