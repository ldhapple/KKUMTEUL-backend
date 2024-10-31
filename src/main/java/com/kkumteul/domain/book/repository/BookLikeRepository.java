package com.kkumteul.domain.book.repository;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookLike;
import com.kkumteul.domain.recommendation.dto.RecommendBookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.Optional;

@Repository
public interface BookLikeRepository extends JpaRepository<BookLike, Long> {

    @Query("SELECT b From BookLike b JOIN FETCH b.book WHERE b.childProfile.id = :childProfileId AND b.likeType = 'LIKE' ")
    List<BookLike> findBookLikesWithBookByChildProfileId(@Param("childProfileId") Long childProfileId);

    @Query("SELECT b FROM BookLike l JOIN l.book b WHERE l.childProfile.id IN :ids AND l.likeType = 'LIKE'")
    Page<Book> findBookLikeByUser(@Param("ids") Set<Long> ids, Pageable pageable);

//    @Query("SELECT b.id FROM BookLike l JOIN l.book b WHERE l.childProfile.id IN :userId AND l.likeType = 'LIKE'")
//    List<Long> findLikedBooksByUser(@Param(value = "userId") Long userId);

    @Query("SELECT b FROM BookLike l JOIN l.book b WHERE l.childProfile.id IN :userId AND l.likeType = 'LIKE'")
    List<Book> findLikedBooksByUser(@Param(value = "userId") Long userId);


    @Query("SELECT b FROM Book b JOIN BookLike bl ON b.id = bl.book.id " +
            "WHERE bl.likeType = 'LIKE' " +
            "GROUP BY b.id " +
            "ORDER BY COUNT(bl.id) DESC")
    List<Book> findTopBooksByLikes(Pageable pageable);

    @Query(value = """
        SELECT bl
          FROM BookLike bl
         WHERE bl.childProfile.id = :childProfileId AND bl.book.id = :bookId
       """)
    Optional<BookLike> findByChildProfileAndBook(
            @Param("childProfileId") Long childProfileId,
            @Param("bookId") Long bookId
    );

    boolean existsByBookIdAndChildProfileId(Long bookId, Long childProfileId);
}

