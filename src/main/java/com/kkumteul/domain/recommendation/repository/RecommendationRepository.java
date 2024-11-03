package com.kkumteul.domain.recommendation.repository;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.recommendation.entity.Recommendation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    @Query("SELECT r.book FROM Recommendation r WHERE r.childProfile.id = :childProfileId")
    Optional<List<Book>> findBookByChildProfileId(Long childProfileId);


    void deleteByChildProfileId(Long id);
}
