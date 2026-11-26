package superbase;

import app_buisness_rules.friend_requests.DenyFriendRequestDataAccessInterface;
import entities.users.Friendship;
import entities.users.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

/** Supabase data-access implementation for denying friend requests. */
public class SupabaseDenyFriendRequestDataAccess
    implements DenyFriendRequestDataAccessInterface {
  private final SupabaseFriendshipDao friendshipDao;

  /**
   * Creates the data-access implementation.
   *
   * @param source database connection provider
   */
  public SupabaseDenyFriendRequestDataAccess(PostgresDataSource source) {
    friendshipDao = new SupabaseFriendshipDao(source);
  }

  @Override
  public Friendship getFriendship(UUID userId, UUID requesterId) {
    try {
      String status = friendshipDao.getStatusByUsers(userId, requesterId);
      if (status == null) {
        return null;
      }
      User requester = new User(requesterId, "", "", "", "", new ArrayList<>());
      User receiver = new User(userId, "", "", "", "", new ArrayList<>());
      return new Friendship(
          requester,
          receiver,
          SupabaseFriendshipStatusMapper.toDomainStatus(status),
          null);
    } catch (SQLException exception) {
      throw new FriendRequestDatabaseException(
          "Unable to find friend request.", exception);
    }
  }

  @Override
  public void deleteFriendship(UUID userId, UUID requesterId) {
    try {
      friendshipDao.deny(userId, requesterId);
    } catch (SQLException exception) {
      throw new FriendRequestDatabaseException(
          "Unable to deny friend request.", exception);
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
