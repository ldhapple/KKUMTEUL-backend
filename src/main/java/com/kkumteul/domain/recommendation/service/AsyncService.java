package com.kkumteul.domain.recommendation.service;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AsyncService {

    private final ChildProfileRepository childProfileRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateLastActivity(Long childProfileId) {
        ChildProfile childProfile = childProfileRepository.findById(childProfileId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        childProfile.updateLastActivity();  // lastActivity 필드 업데이트
        childProfileRepository.save(childProfile);
    }
}