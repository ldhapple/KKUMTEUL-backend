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
}
