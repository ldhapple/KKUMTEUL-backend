package com.kkumteul.exception;

public class AdminBookNotFoundException extends RuntimeException {
    public AdminBookNotFoundException(String input) {
        super("해당 도서를 찾을 수 없습니다. InputBookId: " + input);
    }
}
