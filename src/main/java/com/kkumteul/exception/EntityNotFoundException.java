package com.kkumteul.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Long input) {
        super(String.format("해당하는 엔티티를 찾을 수 없습니다. InputId: %s", input));
    }
}
