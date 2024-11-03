package com.kkumteul.exception;

public class RecommendationBookNotFoundException extends RuntimeException {

    public RecommendationBookNotFoundException(Long input) {
        super(String.format("해당 프로필의 추천 도서를 찾을 수 없습니다. InputProfileId: %s", input));
    }
}
