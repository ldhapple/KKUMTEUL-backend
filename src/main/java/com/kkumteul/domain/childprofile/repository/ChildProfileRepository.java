package com.kkumteul.domain.childprofile.repository;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ChildProfileRepository extends JpaRepository<ChildProfile, Long> {
    Optional<List<ChildProfile>> findByUserId(Long userId);

    @Query("SELECT p FROM ChildProfile p JOIN FETCH p.cumulativeMBTIScore WHERE p.id = :childProfileId")
    Optional<ChildProfile> findByIdWithCumulatvieMBTIScore(@Param("childProfileId") Long childProfileId);
}
