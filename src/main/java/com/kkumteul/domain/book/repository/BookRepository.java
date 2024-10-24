package com.kkumteul.domain.book.repository;

import com.kkumteul.domain.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = """
                SELECT b
                  FROM Book b
                  JOIN FETCH b.bookTopics bt
                  JOIN FETCH bt.topic t
                 ORDER BY b.id ASC
            """)
    Page<Book> findAllBookInfo(final Pageable pageable);

    @Query("""
        SELECT DISTINCT b
        FROM Book b
        LEFT JOIN FETCH b.genre g
        LEFT JOIN FETCH b.bookTopics bt
        LEFT JOIN FETCH bt.topic t
    """)
    List<Book> findAllBooksWithTopicsAndGenre();

    @Query("""
        SELECT DISTINCT b
        FROM Book b
        LEFT JOIN FETCH b.genre g
        LEFT JOIN FETCH b.bookTopics bt
        LEFT JOIN FETCH bt.topic t
        WHERE CAST(SUBSTRING(b.ageGroup, 1, LOCATE('세', b.ageGroup)-1) AS integer) < :age
        ORDER BY :age - CAST(SUBSTRING(b.ageGroup, 1, LOCATE('세', b.ageGroup)-1) AS integer) ASC
    """)
    List<Book> findBookListByAgeGroup(@Param("age") int age, Pageable pageable);
}
