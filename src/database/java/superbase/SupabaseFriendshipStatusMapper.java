package superbase;

import entities.users.FriendshipStatus;

/** Maps friendship status values stored by Supabase to domain statuses. */
final class SupabaseFriendshipStatusMapper {
    private SupabaseFriendshipStatusMapper() {
    }

    static FriendshipStatus toDomainStatus(String databaseStatus) {
        return switch (databaseStatus) {
            case "PENDING" -> FriendshipStatus.SENT_REQUEST;
            case "ADDED" -> FriendshipStatus.ADDED;
            default -> throw new IllegalArgumentException(
                    "Unsupported friendship status from database: " + databaseStatus);
        };
    }
}
