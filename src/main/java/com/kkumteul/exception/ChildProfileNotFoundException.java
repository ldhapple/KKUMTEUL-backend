package com.kkumteul.exception;

public class ChildProfileNotFoundException extends RuntimeException {
    public ChildProfileNotFoundException(Long input) {
        super(String.format("해당 유저의 자녀 프로필을 찾을 수 없습니다. InputUserId: %s", input));
    }
}
