package com.kkumteul.domain.book.exception;

public class BookNotFoundException extends RuntimeException {
  public BookNotFoundException(String message) {
    super("Book not found: " + message);
  }

  public BookNotFoundException(Long bookId) {
    super("Book not found with ID: " + bookId);
  }
}
