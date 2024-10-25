package com.kkumteul.domain.book.controller;

import com.kkumteul.domain.book.dto.GetBookDetailResponseDto;
import com.kkumteul.domain.book.dto.GetBookListResponseDto;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookTopic;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private Book mockBook;
    private GetBookDetailResponseDto mockBookDetailResponseDto;

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

        // 도서 목록 조회 모킹
        GetBookListResponseDto bookDto = new GetBookListResponseDto();
        Page<GetBookListResponseDto> bookPage = new PageImpl<>(Arrays.asList(bookDto));

        // 도서 상세 조회: BookDetailResponseDto 생성
        mockBookDetailResponseDto = GetBookDetailResponseDto.from(mockBook);

        // 키워드가 없는 경우와 있는 경우
        Mockito.when(bookService.getBookList(any(Pageable.class))).thenReturn(bookPage);
        Mockito.when(bookService.getBookList(anyString(), any(Pageable.class))).thenReturn(bookPage);

        // 도서 상세 조회 모킹
        Mockito.when(bookService.getBookDetail(anyLong())).thenReturn(mockBookDetailResponseDto);
    }

    @Test
    @DisplayName("전체 도서 반환: 요청 시 책 목록 Page 당 12개씩 반환한다.")
    void testGetBookList() throws Exception {
        mockMvc.perform(get("/api/books?page=0&size=12")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content").isArray())
                .andExpect(jsonPath("$.response.content.length()").value(1));
    }

    @Test
    @DisplayName("키워드로 도서 목록 조회: 키워드가 포함된 책 목록을 반환한다.")
    void testGetBookListWithKeyword() throws Exception {
        mockMvc.perform(get("/api/books?keyword=테스트&page=0&size=12")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content").isArray())
                .andExpect(jsonPath("$.response.content.length()").value(1));
    }

    @Test
    @DisplayName("도서 상세 조회 성공")
    void getBookDetail_Success() throws Exception {
        // API 호출
        mockMvc.perform(get("/api/books/{bookId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.bookId").value(mockBookDetailResponseDto.getBookId()))
                .andExpect(jsonPath("$.response.bookTitle").value(mockBookDetailResponseDto.getBookTitle()))
                .andExpect(jsonPath("$.response.bookAuthor").value(mockBookDetailResponseDto.getBookAuthor()))
                .andExpect(jsonPath("$.response.bookImage").value(Base64.getEncoder().encodeToString(mockBookDetailResponseDto.getBookImage())))
                .andExpect(jsonPath("$.response.mbtiInfo").value(mockBookDetailResponseDto.getMbtiInfo()))
                .andExpect(jsonPath("$.response.bookSummary").value(mockBookDetailResponseDto.getBookSummary()))
                .andExpect(jsonPath("$.response.genreName").value(mockBookDetailResponseDto.getGenreName()))
                .andExpect(jsonPath("$.response.topicNames[0]").value("주제1"))
                .andExpect(jsonPath("$.response.topicNames[1]").value("주제2"))
                .andExpect(jsonPath("$.response.age_group").value(mockBookDetailResponseDto.getAge_group()))
                .andExpect(jsonPath("$.response.bookPage").value(mockBookDetailResponseDto.getBookPage()))
                .andExpect(jsonPath("$.response.publisher").value(mockBookDetailResponseDto.getPublisher()));
    }

}
