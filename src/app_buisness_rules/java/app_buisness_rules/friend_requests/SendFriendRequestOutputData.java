package app_buisness_rules.friend_requests;

import java.util.UUID;

/** Output data returned after sending a friend request. */
public record SendFriendRequestOutputData(UUID requesterId, UUID receiverId, String status) {
}
