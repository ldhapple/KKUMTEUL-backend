package com.kkumteul.domain.childprofile.repository;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.GenreScore;
import com.kkumteul.domain.personality.entity.Genre;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface GenreScoreRepository extends JpaRepository<GenreScore, Long> {
    List<GenreScore> findByChildProfileId(Long childProfileId);

    @Query("SELECT gs FROM GenreScore gs JOIN FETCH gs.genre WHERE gs.childProfile.id = :childProfileId AND gs.genre.id = :genreId")
    Optional<GenreScore> findByChildProfileAndGenre(@Param("childProfileId") Long childProfileId, @Param("genreId") Long genreId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE GenreScore gs SET gs.score = gs.score + :delta " +
            "WHERE gs.childProfile.id = :childProfileId AND gs.genre.id = :genreId")
    int bulkUpdateScore(@Param("childProfileId") Long childProfileId,
                        @Param("genreId") Long genreId,
                        @Param("delta") Double delta);
}
