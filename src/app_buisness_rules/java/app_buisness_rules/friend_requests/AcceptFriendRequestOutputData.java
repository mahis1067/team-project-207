package app_buisness_rules.friend_requests;

import java.util.UUID;

/** Output data returned after accepting a friend request. */
public record AcceptFriendRequestOutputData(UUID friendshipId, String status) {
}
