package superbase;

import app_buisness_rules.friend_requests.SendFriendRequestDataAccessInterface;
import entities.users.Friendship;
import entities.users.FriendshipFactory;
import entities.users.FriendshipStatus;
import entities.users.User;
import entities.users.UserFactory;
import java.sql.SQLException;
import java.util.Collections;
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
          .map(row -> UserFactory.create(
              row.id(),
              row.username(),
              row.passwordHash(),
              row.firstName(),
              row.lastName(),
              Collections.emptyList()))
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
      User sender = UserFactory.create(senderId, "", "", "", "", Collections.emptyList());
      User receiver = UserFactory.create(receiverId, "", "", "", "", Collections.emptyList());
      return FriendshipFactory.create(
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
