package com.kkumteul.domain.recommendation.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.Gender;
import com.kkumteul.domain.recommendation.entity.Recommendation;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RecommendationRepositoryTest {

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private EntityManager entityManager;

    private ChildProfile childProfile;
    private Book book;

    @BeforeEach
    void setUp() {
        childProfile = ChildProfile.builder()
                .name("홍길동")
                .gender(Gender.FEMALE)
                .profileImage(new byte[]{})
                .build();

        book = Book.builder()
                .title("Title")
                .author("Author")
                .publisher("Publisher")
                .price("Price")
                .page("Page")
                .summary("Summary")
                .bookImage(new byte[]{})
                .build();

        Recommendation recommendation = Recommendation.builder()
                .book(book)
                .childProfile(childProfile)
                .build();

        entityManager.persist(childProfile);
        entityManager.persist(book);
        entityManager.persist(recommendation);
    }

    @Test
    @DisplayName("자녀 프로필 아이디로 추천 책 조회 성공")
    void testFindRecommendedBooks() {
        Optional<List<Book>> findBooks = recommendationRepository.findBookByChildProfileId(
                childProfile.getId());

        assertThat(findBooks).isPresent();
        assertThat(findBooks.get()).hasSize(1);
        assertThat(findBooks.get().get(0)).isEqualTo(book);
    }

    @Test
    @DisplayName("자녀 프로필에 추천된 책이 없을 경우 테스트")
    void testRecommendationBookNotFoundException() {
        ChildProfile childProfileHasNotRecommendation = ChildProfile.builder()
                .build();

        entityManager.persist(childProfileHasNotRecommendation);

        Optional<List<Book>> findBooks = recommendationRepository.findBookByChildProfileId(
                childProfileHasNotRecommendation.getId());

        assertThat(findBooks.get()).isEmpty();
    }
}