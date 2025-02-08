package org.vicky.vicky.utilities.enums;

public enum FriendSortingStyles {
    LAST_ONLINE,
    LAST_ONLINE_DESCENDING,
    ADDED_TIME,
    ADDED_TIME_DESCENDING,
    RANK,
    RANK_DESCENDING;

    public boolean isDescending() {
        return name().contains("DESCENDING");
    }
}
