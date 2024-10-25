package com.kkumteul.util.redis;

public enum RedisKey {
    BOOK_LIKE_EVENT_LIST("BookLikeEventList");

    private final String key;

    RedisKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
