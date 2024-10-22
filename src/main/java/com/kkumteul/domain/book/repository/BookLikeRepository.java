package com.kkumteul.domain.book.repository;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookLike;
import com.kkumteul.domain.recommendation.dto.BookDataDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

@Repository
public interface BookLikeRepository extends JpaRepository<BookLike, Long> {
    @Query("SELECT b FROM BookLike l JOIN l.book b WHERE l.childProfile.id IN :ids AND l.likeType = 'LIKE'")
    Page<Book> findBookLikeByUser(@Param("ids") Set<Long> ids, Pageable pageable);
}
