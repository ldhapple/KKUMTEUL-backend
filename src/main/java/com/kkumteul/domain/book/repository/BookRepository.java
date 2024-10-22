package com.kkumteul.domain.book.repository;

import com.kkumteul.domain.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("""
        SELECT DISTINCT b
        FROM Book b
        LEFT JOIN FETCH b.genre g
        LEFT JOIN FETCH b.bookTopics bt
        LEFT JOIN FETCH bt.topic t
    """)
    List<Book> findAllBooksWithTopicsAndGenre();

}
