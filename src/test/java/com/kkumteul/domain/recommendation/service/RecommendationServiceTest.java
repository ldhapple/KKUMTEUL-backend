package com.kkumteul.domain.recommendation.service;

import static com.kkumteul.domain.childprofile.entity.Gender.MALE;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.repository.BookRepository;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.recommendation.dto.RecommendBookDto;
import com.kkumteul.domain.recommendation.entity.Recommendation;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private BookRepository bookRepository;

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

    @Test
    @DisplayName("기본 추천 로직 테스트")
    void testDefaultRecommend() {
        // 테스트 데이터 설정 및 DB 삽입
        Book book1 = new Book("Title1", "Author", "Publisher", "Price", "Page", "4세부터", "Summary", new byte[]{}, null, null, null);
        Book book2 = new Book("Title2", "Author", "Publisher", "Price", "Page", "5세부터", "Summary", new byte[]{}, null, null, null);

        Pageable pageable = PageRequest.of(0, 20);
        List<Book> books = List.of(book2, book1);

        given(bookRepository.findBookListByAgeGroup(10, pageable)).willReturn(books);

        List<Book> results = recommendationService.getDefaultRecommendations(10);

        // 10살이니까 book2가 더 위에 있음
        assertEquals("Title2", results.get(0).getTitle());
        assertEquals("Title1", results.get(1).getTitle());
    }

}