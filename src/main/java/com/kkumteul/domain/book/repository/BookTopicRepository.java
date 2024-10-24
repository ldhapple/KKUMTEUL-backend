package com.kkumteul.domain.book.repository;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookTopicRepository extends JpaRepository<BookTopic, Long > {
    List<BookTopic> findByBook(Book book);
}
