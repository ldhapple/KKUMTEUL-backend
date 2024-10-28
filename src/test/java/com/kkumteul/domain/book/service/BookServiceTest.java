package com.kkumteul.domain.book.service;

import com.kkumteul.domain.book.dto.GetBookDetailResponseDto;
import com.kkumteul.domain.book.dto.GetBookListResponseDto;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookLike;
import com.kkumteul.domain.book.entity.BookTopic;
import com.kkumteul.domain.book.entity.LikeType;
import com.kkumteul.domain.book.exception.BookNotFoundException;
import com.kkumteul.domain.book.repository.BookLikeRepository;
import com.kkumteul.domain.book.repository.BookRepository;
import com.kkumteul.domain.book.service.impl.BookServiceImpl;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookLikeRepository bookLikeRepository;

    @Mock
    private ChildProfileRepository childProfileRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book mockBook;
    private BookLike bookLike;

    @BeforeEach
    void setUp() {
        // Mock Book 데이터 생성
        mockBook = Book.builder()
                .title("테스트 도서 제목")
                .author("테스트 저자")
                .bookImage(new byte[]{})
                .summary("테스트 도서 요약")
                .genre(new Genre("소설", null))
                .age_group("12세 부터")
                .page("300")
                .publisher("테스트 출판사")
                .bookMBTIS(new ArrayList<>())
                .bookTopics(Arrays.asList(
                        new BookTopic(mockBook, Topic.builder().name("주제1").build()),
                        new BookTopic(mockBook, Topic.builder().name("주제2").build())))
                .build();

        bookLike = BookLike.builder()
                .book(mockBook)
                .likeType(LikeType.LIKE)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("책 목록 조회")
    void getBookList_ShouldReturnBookList() {
        // given
        Pageable pageable = Pageable.ofSize(10);
        when(bookRepository.findAllBookInfo(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(mockBook)));

        // when
        Page<GetBookListResponseDto> result = bookService.getBookList(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBookTitle()).isEqualTo(mockBook.getTitle());
    }

    @Test
    @DisplayName("책 상세 조회")
    void getBookDetail_ShouldReturnBookDetail() {
        // given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));

        // when
        GetBookDetailResponseDto result = bookService.getBookDetail(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getBookTitle()).isEqualTo(mockBook.getTitle());
    }

    @Test
    @DisplayName("책 상세 조회 시 책이 없으면 예외 발생")
    void getBookDetail_BookNotFound_ShouldThrowException() {
        // given
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> bookService.getBookDetail(1L))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("좋아요 추가 시 새로운 좋아요 생성")
    void bookLike_ShouldCreateNewLike() {
        // given
        ChildProfile childProfile = ChildProfile.builder().name("자녀 1").build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));
        when(childProfileRepository.findById(1L)).thenReturn(Optional.of(childProfile));
        when(bookLikeRepository.findByChildProfileAndBook(1L, 1L)).thenReturn(Optional.empty());

        // when
        bookService.bookLike(1L, 1L, LikeType.LIKE);

        // then
        ArgumentCaptor<BookLike> bookLikeCaptor = ArgumentCaptor.forClass(BookLike.class);
        verify(bookLikeRepository).save(bookLikeCaptor.capture());
        assertThat(bookLikeCaptor.getValue().getLikeType()).isEqualTo(LikeType.LIKE);
    }

}
