package com.kkumteul.domain.childprofile.service;

import com.kkumteul.domain.childprofile.dto.ChildProfileDto;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.exception.ChildProfileNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChildProfileService {

    private final ChildProfileRepository childProfileRepository;

    public List<ChildProfileDto> getChildProfile(Long userId) {
        log.info("getChildProfiles - Input userId: {}", userId);
        List<ChildProfile> childProfiles = childProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ChildProfileNotFoundException(userId));

        log.info("found childProfiles: {}", childProfiles.size());
        return childProfiles.stream()
                .map(ChildProfileDto::fromEntity)
                .toList();
    }

    public void validateChildProfile(Long childProfileId) {
        log.info("validate exist childProfile: {}", childProfileId);
        childProfileRepository.findById(childProfileId).orElseThrow(
                () -> new IllegalArgumentException("childProfile not found - childProfileId : " + childProfileId));
    }
}
