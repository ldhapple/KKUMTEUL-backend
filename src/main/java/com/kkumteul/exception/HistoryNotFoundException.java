package com.kkumteul.exception;

public class HistoryNotFoundException extends RuntimeException {
    public HistoryNotFoundException(Long input) {
        super(String.format("히스토리를 찾을 수 없습니다. InputId: %s", input));
    }
}
