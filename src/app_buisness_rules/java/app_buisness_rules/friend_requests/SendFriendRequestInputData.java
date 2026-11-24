package app_buisness_rules.friend_requests;

import java.util.UUID;

/** Input data for sending a friend request. */
public record SendFriendRequestInputData(UUID senderId, String receiverUsername) {
}
