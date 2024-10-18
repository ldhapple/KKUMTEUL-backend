package com.kkumteul.domain.recommendation.service;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.recommendation.dto.RecommendBookDto;
import com.kkumteul.domain.recommendation.repository.RecommendationRepository;
import com.kkumteul.exception.RecommendationBookNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public List<RecommendBookDto> getRecommendedBooks(Long childProfileId) {
        List<Book> recommendBooks = recommendationRepository.findBookByChildProfileId(childProfileId)
                .orElseThrow(() -> new RecommendationBookNotFoundException(childProfileId));

        return recommendBooks.stream()
                .map(RecommendBookDto::fromEntity)
                .toList();
    }
}
