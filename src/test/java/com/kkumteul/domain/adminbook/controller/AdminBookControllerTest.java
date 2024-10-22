package com.kkumteul.domain.adminbook.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkumteul.domain.book.controller.AdminBookController;
import com.kkumteul.domain.book.dto.AdminInsertBookRequestDto;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.entity.MBTIName;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminBookController.class)
public class AdminBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper; // JSON 변환에 사용

    @Test
    @DisplayName("도서 등록 API 성공 테스트")
    void testAdminInsertBook() throws Exception {
        // Given: 테스트에 사용할 더미 데이터 생성
        AdminInsertBookRequestDto requestDto = new AdminInsertBookRequestDto(
                "Sample Book Title",
                "John Doe",
                "Sample Publisher",
                "25000",
                "350",
                "8세",
                "This is a sample summary for the book.",
                "U29tZUJhc2U2NEVuY29kZWRJbWFnZURhdGE=".getBytes(),

                new MBTI(
                        MBTIName.valueOf("INTJ"),
                        "The Architect",
                        "Analytical and logical, often focused on the bigger picture.",
                        "U29tZUJhc2U2NEVuY29kZWRJbWFnZURhdGE=".getBytes()
                ),
                List.of(
                        new Genre(
                                "Fiction",
                                "U29tZUJhc2U2NEVuY29kZWRJbWFnZURhdGE=".getBytes()
                        ),

                        new Genre(
                                "Adventure",
                                "U29tZUJhc2U2NEVuY29kZWRJbWFnZURhdGE=".getBytes()
                        )
                ),
                List.of(
                        new Topic(
                                "Leadership",
                                "U29tZUJhc2U2NEVuY29kZWRJbWFnZURhdGE=".getBytes()
                        ),
                        new Topic(
                                "Self-Development",
                                "U29tZUJhc2U2NEVuY29kZWRJbWFnZURhdGE=".getBytes()
                        )
                )
        );

        // Mocking: bookService.insertBook 호출 시, 응답은 단순 성공 메시지로 설정
        given(bookService.insertBook(requestDto)).willReturn(null);

        // When & Then: 요청 실행 및 기대 응답 검증
        mockMvc.perform(post("/api/admin/books")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("book insert successfully"));
    }

}
