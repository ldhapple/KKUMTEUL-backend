package com.kkumteul.domain.book.repository;

import com.kkumteul.domain.book.dto.GetBookListResponseDto;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookTopic;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @MockBean
    private BookService bookService;

    @Autowired
    private EntityManager em;

    private Book mockBook;

    @BeforeEach
    void setUp() {
        Genre genre = new Genre("소설", null);
        em.persist(genre);
        em.flush();

        Topic topic1 = Topic.builder().name("주제1").build();
        Topic topic2 = Topic.builder().name("주제2").build();
        em.persist(topic1);
        em.persist(topic2);
        em.flush();


        mockBook = Book.builder()
                .title("테스트 도서 제목")
                .author("테스트 저자")
                .bookImage(new byte[] {})
                .summary("테스트 도서 요약")
                .genre(genre)
                .ageGroup("12세 부터")
                .page("300")
                .publisher("테스트 출판사")
                .bookTopics(new ArrayList<>())
                .build();

        em.persist(mockBook);
        em.flush();

        mockBook.getBookTopics().add(new BookTopic(mockBook, topic1));
        mockBook.getBookTopics().add(new BookTopic(mockBook, topic2));
        em.persist(mockBook);
        em.flush();

        // 도서 목록 조회 모킹
        GetBookListResponseDto bookDto = new GetBookListResponseDto();
        Page<GetBookListResponseDto> bookPage = new PageImpl<>(Arrays.asList(bookDto));

        // 키워드가 없는 경우와 있는 경우
        Mockito.when(bookService.getBookList(any(Pageable.class))).thenReturn(bookPage);
        Mockito.when(bookService.getBookList(anyString(), any(Pageable.class))).thenReturn(bookPage);
    }


    @Test
    @WithMockUser
    @DisplayName("전체 도서 반환: 요청 시 책 목록 Page 당 12개씩 반환한다.")
    void testFindAllBookInfo() {
        // Given
        Pageable pageable = PageRequest.of(0, 12);

        // When
        Page<Book> result = bookRepository.findAllBookInfo(pageable);
        System.out.println("책 반환 수: " + result.getContent().size());

        // Then
        assertThat(result.getTotalElements()).isGreaterThan(0);
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getTitle()).isEqualTo(mockBook.getTitle());
    }

    @Test
    @WithMockUser
    @DisplayName("키워드로 도서 검색: 주어진 키워드에 해당하는 도서 목록을 반환한다.")
    void testFindBookListByKeyword() {
        String keyword = "테스트";
        Pageable pageable = PageRequest.of(0, 12);
        Page<Book> result = bookRepository.findBookListByKeyword(keyword, pageable);

        assertThat(result.getTotalElements()).isGreaterThan(0);
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getTitle()).isEqualTo(mockBook.getTitle());
    }
}
