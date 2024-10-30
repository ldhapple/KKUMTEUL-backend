package com.kkumteul.domain.childprofile.repository;

import com.kkumteul.domain.childprofile.entity.CumulativeMBTIScore;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CumulativeMBTIScoreRepository extends JpaRepository<CumulativeMBTIScore, Long> {

    Optional<CumulativeMBTIScore> findByChildProfileId(Long childProfileId);
}
