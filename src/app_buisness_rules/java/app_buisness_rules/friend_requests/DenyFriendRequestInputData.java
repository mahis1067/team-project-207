package app_buisness_rules.friend_requests;

import java.util.UUID;

/** Input data for denying a friend request. */
public record DenyFriendRequestInputData(UUID userId, UUID requesterId) {
}
