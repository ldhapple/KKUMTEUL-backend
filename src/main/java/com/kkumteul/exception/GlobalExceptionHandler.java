package com.kkumteul.exception;


import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiError;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<?> handleUserNotFoundException(Exception e) {
        log.error(e.getMessage(), e);
        ApiError<String> error = ApiUtil.error(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(error);
    }
}