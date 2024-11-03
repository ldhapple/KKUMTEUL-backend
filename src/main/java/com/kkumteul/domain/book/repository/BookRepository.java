package com.kkumteul.domain.book.repository;

import com.kkumteul.domain.book.entity.Book;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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
                 ORDER BY b.id ASC
            """)
    Page<Book> findAllBookInfo(final Pageable pageable);

    @EntityGraph(attributePaths = {"genre", "bookTopics.topic"})
    @Query("SELECT b FROM Book b")
    List<Book> findAllBooksWithTopicsAndGenre();

    @Query("""
        SELECT DISTINCT b
        FROM Book b
        WHERE CAST(SUBSTRING(b.ageGroup, 1, LOCATE('세', b.ageGroup)-1) AS integer) < :age
        ORDER BY :age - CAST(SUBSTRING(b.ageGroup, 1, LOCATE('세', b.ageGroup)-1) AS integer) ASC
    """)
    List<Book> findBookListByAgeGroup(@Param("age") int age, Pageable pageable);

    @Query(value = """
            SELECT b
              FROM Book b
             WHERE b.title LIKE %:keyword%
                OR b.author LIKE %:keyword%
            """)
    Page<Book> searchByTitleOrAuthor(@Param("keyword") String keyword, final Pageable pageable);

    @Query("SELECT b FROM Book b JOIN FETCH b.genre JOIN FETCH b.bookTopics WHERE b.id = :bookId")
    Optional<Book> findBookByIdWithGenreAndTopic(@Param("bookId") Long bookId);

    @Query(value = """
        SELECT b FROM Book b
        JOIN b.genre bg
        JOIN b.bookTopics bt
        JOIN bt.topic t
        WHERE b.title LIKE CONCAT('%', :keyword, '%')
        OR b.author LIKE CONCAT('%', :keyword, '%')
        OR t.name LIKE CONCAT('%', :keyword, '%')
        OR bg.name LIKE CONCAT('%', :keyword, '%')
        ORDER BY CASE
        WHEN b.title LIKE CONCAT('%', :keyword, '%') THEN 0
        WHEN b.author LIKE CONCAT('%', :keyword, '%') THEN 1
        WHEN t.name LIKE CONCAT('%', :keyword, '%') THEN 2
        WHEN bg.name LIKE CONCAT('%', :keyword, '%') THEN 3
        ELSE 4 END, b.id ASC
        """)
    Page<Book> findBookListByKeyword(
            @Param("keyword") final String keyword,
            final Pageable pageable);

}
