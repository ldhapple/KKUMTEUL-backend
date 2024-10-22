package com.kkumteul.domain.recommendation.service;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.recommendation.dto.RecommendBookDto;
import com.kkumteul.domain.recommendation.repository.RecommendationRepository;
import com.kkumteul.exception.RecommendationBookNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public List<RecommendBookDto> getRecommendedBooks(Long childProfileId) {
        log.info("getRecommendedBooks - Input childProfileId: {}", childProfileId);
        List<Book> recommendBooks = recommendationRepository.findBookByChildProfileId(childProfileId)
                .orElseThrow(() -> new RecommendationBookNotFoundException(childProfileId));

        log.info("found recommendedBooks: {}", recommendBooks.size());
        return recommendBooks.stream()
                .map(RecommendBookDto::fromEntity)
                .toList();
    }
}
