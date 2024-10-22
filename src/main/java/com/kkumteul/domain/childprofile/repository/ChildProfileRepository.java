package com.kkumteul.domain.childprofile.repository;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildProfileRepository extends JpaRepository<ChildProfile, Long> {

    Optional<List<ChildProfile>> findByUserId(Long userId);
}
