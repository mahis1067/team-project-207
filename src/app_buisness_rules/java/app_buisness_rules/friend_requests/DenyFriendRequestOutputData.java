package app_buisness_rules.friend_requests;

import java.util.UUID;

/** Output data returned after denying a friend request. */
public record DenyFriendRequestOutputData(UUID requesterId) {
}
