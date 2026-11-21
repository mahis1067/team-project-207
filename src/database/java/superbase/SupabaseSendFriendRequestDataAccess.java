package superbase;

import app_buisness_rules.friend_requests.SendFriendRequestDataAccessInterface;
import entities.users.Friendship;
import entities.users.FriendshipStatus;
import entities.users.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

/** Supabase data-access implementation for sending friend requests. */
public class SupabaseSendFriendRequestDataAccess
    implements SendFriendRequestDataAccessInterface {
  private final SupabaseUserDao userDao;
  private final SupabaseFriendshipDao friendshipDao;

  /**
   * Creates the data-access implementation.
   *
   * @param source database connection provider
   */
  public SupabaseSendFriendRequestDataAccess(PostgresDataSource source) {
    userDao = new SupabaseUserDao(source);
    friendshipDao = new SupabaseFriendshipDao(source);
  }

  @Override
  public User getUserByUsername(String username) {
    try {
      return userDao.findByUsername(username)
          .map(row -> new User(
              row.id(),
              row.username(),
              row.passwordHash(),
              row.firstName(),
              row.lastName(),
              new ArrayList<>()))
          .orElse(null);
    } catch (SQLException exception) {
      throw new FriendRequestDatabaseException(
          "Unable to find receiver.", exception);
    }
  }

  @Override
  public Friendship getFriendship(UUID senderId, UUID receiverId) {
    try {
      String status =
          friendshipDao.getStatusByUsers(receiverId, senderId);
      if (status == null) {
        status = friendshipDao.getStatusByUsers(senderId, receiverId);
      }
      if (status == null) {
        return null;
      }
      User sender = new User(senderId, "", "", "", "", new ArrayList<>());
      User receiver = new User(receiverId, "", "", "", "", new ArrayList<>());
      return new Friendship(
          sender,
          receiver,
          FriendshipStatus.valueOf(status),
          null);
    } catch (SQLException exception) {
      throw new FriendRequestDatabaseException(
          "Unable to find friendship.", exception);
    }
  }

  @Override
  public void saveFriendship(Friendship friendship) {
    try {
      friendshipDao.sendRequest(
          friendship.getRequestedBy().getUserId(),
          friendship.getSentTo().getUserId());
    } catch (SQLException exception) {
      throw new FriendRequestDatabaseException(
          "Unable to send friend request.", exception);
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
