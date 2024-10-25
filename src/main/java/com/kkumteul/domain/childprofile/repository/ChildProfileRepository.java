package com.kkumteul.domain.childprofile.repository;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ChildProfileRepository extends JpaRepository<ChildProfile, Long> {
    Optional<List<ChildProfile>> findByUserId(Long userId);

    @Query("SELECT c.id FROM ChildProfile c WHERE c.lastActivity >= :threshold")
    List<Long> findActiveUserIdsLast7Days(@Param("threshold") LocalDateTime threshold);
}
