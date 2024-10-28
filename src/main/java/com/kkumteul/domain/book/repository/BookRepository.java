package com.kkumteul.domain.book.repository;

import com.kkumteul.domain.book.entity.Book;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = """
                SELECT b
                  FROM Book b
                  JOIN FETCH b.bookTopics bt
                  JOIN FETCH bt.topic t
                 ORDER BY b.id ASC
            """)
    Page<Book> findAllBookInfo(final Pageable pageable);

    @Query("SELECT b FROM Book b JOIN FETCH b.genre JOIN FETCH b.bookTopics WHERE b.id = :bookId")
    Optional<Book> findBookByIdWithGenreAndTopic(@Param("bookId") Long bookId);

    @Query(value = """
        SELECT DISTINCT b
          FROM Book b
          JOIN FETCH b.genre bg
          JOIN FETCH b.bookTopics bt
          JOIN FETCH bt.topic t
         WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(bg.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
         ORDER BY
            CASE
                WHEN LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) THEN 0
                WHEN LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) THEN 1
                WHEN LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) THEN 2
                WHEN LOWER(bg.name) LIKE LOWER(CONCAT('%', :keyword, '%')) THEN 3
                ELSE 4
            END, b.id ASC
        """)
    Page<Book> findBookListByKeyword(
            @Param("keyword") final String keyword,
            final Pageable pageable);
}
