package com.kkumteul.domain.childprofile.service;

import com.kkumteul.domain.book.dto.BookLikeDto;
import com.kkumteul.domain.book.repository.BookLikeRepository;
import com.kkumteul.domain.childprofile.dto.ChildProfileResponseDto;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.history.dto.ChildPersonalityHistoryDto;
import com.kkumteul.domain.history.repository.ChildPersonalityHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChildProfileService {
    private final ChildProfileRepository childProfileRepository;
    private final ChildPersonalityHistoryRepository childPersonalityHistoryRepository;
    private final BookLikeRepository bookLikeRepository;

    // 1. 자녀 프로필 상세 조회
    public ChildProfileResponseDto getChildProfileDetail(Long childProfileId) {
        log.info("childProfile id: {}", childProfileId);

        ChildProfile childProfile = childProfileRepository.findById(childProfileId)
                // TODO: 전역 예외 처리로 변경하기
                .orElseThrow(() -> new IllegalArgumentException("child profile not found"));
        log.info("Child profile found: {}", childProfile.getName());


        // 1-1. 자녀가 좋아하는 도서 리스트
        List<BookLikeDto> likedBooks = bookLikeRepository.findBookLikesWithBookByChildProfileId(childProfileId).stream()
                .map(BookLikeDto::fromEntity)
                .toList();
        log.info("Number of books liked by child (childProfile iD: {}): {}", childProfileId, likedBooks.size());


        // 1-2. 자녀 성향 진단 및 변화 히스토리
        List<ChildPersonalityHistoryDto> childPersonalityHistories = childPersonalityHistoryRepository.findHistoryWithMBTIByChildProfileId(childProfileId).stream()
                .map(ChildPersonalityHistoryDto::fromEntity)
                .toList();
        log.info("Number of personality histories by child (childProfile iD: {}): {}", childProfileId, childPersonalityHistories.size());

        return new ChildProfileResponseDto(childProfile.getName(), likedBooks, childPersonalityHistories);

    }
}
