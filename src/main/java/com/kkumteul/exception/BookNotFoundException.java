package com.kkumteul.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long input) {
        super(String.format("해당 도서를 찾을 수 없습니다. InputBookId: %s", input));
    }
}
