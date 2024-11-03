package com.kkumteul.exception;

public class AdminEventNotFoundException extends RuntimeException {
    public AdminEventNotFoundException(String input) {
        super("해당 이벤트를 찾을 수 없습니다. EventId: " + input);
    }
}
