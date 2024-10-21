package com.kkumteul.domain.book.repository;

import com.kkumteul.domain.book.entity.BookLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookLikeRepository extends JpaRepository<BookLike, Long> {

    @Query("SELECT b From BookLike b LEFT JOIN FETCH b.book WHERE b.childProfile.id = :childProfileId")
    List<BookLike> findBookLikesWithBookByChildProfileId(@Param("childProfileId") Long childProfileId);

}
