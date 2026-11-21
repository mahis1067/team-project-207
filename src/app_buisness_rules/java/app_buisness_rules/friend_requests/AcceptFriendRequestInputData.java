package app_buisness_rules.friend_requests;

import java.util.UUID;

/** Input data for accepting a friend request. */
public record AcceptFriendRequestInputData(UUID friendshipId) {
}
