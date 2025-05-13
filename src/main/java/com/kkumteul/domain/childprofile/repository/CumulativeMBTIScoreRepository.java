package com.kkumteul.domain.childprofile.repository;

import com.kkumteul.domain.childprofile.entity.CumulativeMBTIScore;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CumulativeMBTIScoreRepository extends JpaRepository<CumulativeMBTIScore, Long> {

    Optional<CumulativeMBTIScore> findByChildProfileId(Long childProfileId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE CumulativeMBTIScore cm SET " +
            "cm.eScore = cm.eScore + :delta, " +
            "cm.fScore = cm.fScore + :delta, " +
            "cm.iScore = cm.iScore + :delta, " +
            "cm.jScore = cm.jScore + :delta, " +
            "cm.nScore = cm.nScore + :delta, " +
            "cm.pScore = cm.pScore + :delta, " +
            "cm.sScore = cm.sScore + :delta, " +
            "cm.tScore = cm.tScore + :delta " +
            "WHERE cm.childProfile.id = :childProfileId")
    int bulkUpdateScore(@Param("childProfileId") Long childProfileId,
                        @Param("delta") Double delta);
}
