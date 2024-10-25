package com.kkumteul.domain.book.repository;

import com.kkumteul.domain.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // 민아님 코드와 동일
    @Query(value = """
                SELECT b
                  FROM Book b
                  JOIN FETCH b.bookTopics bt
                  JOIN FETCH bt.topic t
                 ORDER BY b.id ASC
            """)
    Page<Book> findAllBookInfo(final Pageable pageable);

    @Query(value = """
            SELECT b
              FROM Book b
             WHERE b.title LIKE %:keyword%
                OR b.author LIKE %:keyword%
            """)
    Page<Book> searchByTitleOrAuthor(@Param("keyword") String keyword, final Pageable pageable);
}
