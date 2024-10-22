package com.kkumteul.exception;

public class InvalidMBTINameException extends RuntimeException {

    public InvalidMBTINameException(String inputMbtiName) {
        super(String.format("해당하는 MBTI가 없습니다. InputMBTIName: %s", inputMbtiName));
    }
}
