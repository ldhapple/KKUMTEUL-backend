package com.kkumteul.util.kafka;

public enum KafkaTopic {
    BOOK_LIKE("like-event"),
    BOOK_DISLIKE("dislike-event");

    private final String topicName;

    KafkaTopic(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }

    public static final String BOOK_LIKE_TOPIC = "like-event";
    public static final String BOOK_DISLIKE_TOPIC = "dislike-event";
}
