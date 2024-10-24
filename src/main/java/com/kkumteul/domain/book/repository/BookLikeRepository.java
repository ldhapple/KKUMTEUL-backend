package com.kkumteul.domain.book.repository;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookLikeRepository extends JpaRepository<BookLike, Long> {

    @Query("SELECT b From BookLike b JOIN FETCH b.book WHERE b.childProfile.id = :childProfileId")
    List<BookLike> findBookLikesWithBookByChildProfileId(@Param("childProfileId") Long childProfileId);

    @Query("SELECT b FROM BookLike l JOIN l.book b WHERE l.childProfile.id IN :ids AND l.likeType = 'LIKE'")
    Page<Book> findBookLikeByUser(@Param("ids") Set<Long> ids, Pageable pageable);
}

