package com.kkumteul.domain.recommendation.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.recommendation.dto.RecommendBookDto;
import com.kkumteul.domain.recommendation.repository.RecommendationRepository;
import com.kkumteul.exception.RecommendationBookNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    @DisplayName("자녀 프로필에 따른 추천 도서 가져오기 성공 테스트")
    void testGetRecommendedBooks() {

        Long childProfileId = 1L;
        Book book = Book.builder()
                .title("Title")
                .author("Author")
                .publisher("Publisher")
                .price("Price")
                .page("Page")
                .summary("Summary")
                .bookImage(new byte[]{})
                .build();

        List<Book> books = List.of(book);

        given(recommendationRepository.findBookByChildProfileId(childProfileId)).willReturn(Optional.of(books));

        List<RecommendBookDto> results = recommendationService.getRecommendedBooks(childProfileId);

        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getBookTitle()).isEqualTo("Title");
    }

    @Test
    @DisplayName("추천 도서가 없을 때 예외 발생 테스트")
    void testGetRecommendedBooksNotFound() {
        Long childProfileId = 1L;

        given(recommendationRepository.findBookByChildProfileId(childProfileId)).willReturn(Optional.empty());

        assertThrows(RecommendationBookNotFoundException.class, () ->
                recommendationService.getRecommendedBooks(childProfileId)
        );
    }
}