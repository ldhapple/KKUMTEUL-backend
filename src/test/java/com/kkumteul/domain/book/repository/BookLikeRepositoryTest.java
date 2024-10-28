package com.kkumteul.domain.book.repository;


import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookLike;
import com.kkumteul.domain.book.entity.LikeType;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.Gender;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookLikeRepositoryTest {

    @Autowired
    private ChildProfileRepository childProfileRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookLikeRepository bookLikeRepository;

    private ChildProfile childProfile;
    private Book book;
    private BookLike bookLike;

    @BeforeEach
    void setUp() {
        // Mock ChildProfile 생성
        childProfile = ChildProfile.builder()
                .name("테스트 아동 프로필")
                .gender(Gender.FEMALE)
                .birthDate(java.sql.Date.valueOf("2010-01-01"))
                .build();
        childProfileRepository.save(childProfile);

        // Mock Book 생성
        book = Book.builder()
                .title("테스트 도서 제목")
                .author("테스트 저자")
                .publisher("테스트 출판사")
                .price("12000")
                .page("300")
                .ageGroup("7세 이상")
                .summary("테스트 요약")
                .build();
        bookRepository.save(book);

        // Mock BookLike 생성
        bookLike = BookLike.builder()
                .likeType(LikeType.LIKE)
                .childProfile(childProfile)
                .book(book)
                .updatedAt(LocalDateTime.now())
                .build();
        bookLikeRepository.save(bookLike);
    }

    @Test
    @DisplayName("책 목록 조회: 아동 프로필 ID로 좋아요한 책 목록을 반환한다.")
    void testFindBookLikesWithBookByChildProfileId() {
        // when
        List<BookLike> bookLikes = bookLikeRepository.findBookLikesWithBookByChildProfileId(childProfile.getId());

        // then
        assertThat(bookLikes).isNotEmpty();
        assertThat(bookLikes.get(0).getBook()).isEqualTo(book);
    }


    @Test
    @DisplayName("책 좋아요 조회: 아동 프로필 ID와 도서 ID로 특정 좋아요 정보를 반환한다.")
    void testFindByChildProfileAndBook() {
        // when
        Optional<BookLike> result = bookLikeRepository.findByChildProfileAndBook(childProfile.getId(), book.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(bookLike);
    }

    @Test
    @DisplayName("책 좋아요 조회: 존재하지 않는 도서 ID로 좋아요를 조회하면 결과가 없음을 반환한다.")
    void testFindByChildProfileAndBook_NonExistentBookLike() {
        // when
        Optional<BookLike> result = bookLikeRepository.findByChildProfileAndBook(childProfile.getId(), 999L);

        // then
        assertThat(result).isNotPresent();
    }
}
