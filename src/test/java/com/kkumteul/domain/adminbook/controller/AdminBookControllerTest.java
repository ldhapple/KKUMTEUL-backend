package com.kkumteul.domain.adminbook.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkumteul.domain.book.controller.AdminBookController;
import com.kkumteul.domain.book.dto.AdminBookRequestDto;
import com.kkumteul.domain.book.dto.AdminGetBookDetailResponseDto;
import com.kkumteul.domain.book.dto.AdminGetBookListResponseDto;
import com.kkumteul.domain.book.service.AdminBookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(AdminBookController.class)
@WithMockUser(username = "ROLE_ADMIN")
public class AdminBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminBookService bookService;

    @Autowired
    private ObjectMapper objectMapper; // JSON 변환에 사용

    @Test
    @DisplayName("도서 등록 API 성공 테스트")
    void testAdminInsertBook() throws Exception {

        MockMultipartFile image = new MockMultipartFile("image", "book.jpg", MediaType.IMAGE_JPEG_VALUE, "image data".getBytes());
        AdminBookRequestDto book = new AdminBookRequestDto(
                "홍길동전",
                "미상",
                "꿈틀 출판사",
                "22000",
                "100",
                "6세",
                "아버지를 아버지라 부르지 못하고..",
                "ESFJ",
                "동화",
                List.of("영웅", "인물", "가족")
        );

        String bookJson = objectMapper.writeValueAsString(book);

        mockMvc.perform(multipart("/api/admin/books")
                        .file(image)
                        .file(new MockMultipartFile("book", "", MediaType.APPLICATION_JSON_VALUE, bookJson.getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("book insert successfully"));
    }

    @Test
    @DisplayName("도서목록 조회 API 성공 테스트")
    void getAdminBookListTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AdminGetBookListResponseDto> bookList = new PageImpl<>(Collections.singletonList(
                new AdminGetBookListResponseDto(7L, new byte[0], "홍길동전", "미상", "꿈틀출판사",
                        "동화", "5세", List.of("영웅", "인물", "가족"), "ESFJ")));

        when(bookService.getAdminBookList(any(Pageable.class))).thenReturn(bookList);

        // bookList.getContent()를 통해 List로 변환 후 출력
        bookList.getContent().forEach(book -> System.out.println("도서 리스트: " + book));

        mockMvc.perform(get("/api/admin/books")
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content[0].title").value("홍길동전"));
    }

    @Test
    @DisplayName("도서상세 조회 API 성공 테스트")
    void getAdminBookDetailTest() throws Exception {
        AdminGetBookDetailResponseDto bookDetail = new AdminGetBookDetailResponseDto(1L, new byte[0], "Test Book", "Test Publisher", "Test Author", "1000", "All", "Fiction", List.of("Adventure"), "INTJ", "Test Summary", "100");

        when(bookService.getBookDetailById(1L)).thenReturn(bookDetail);

        mockMvc.perform(get("/api/admin/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.title").value("Test Book"));
    }

    @Test
    @DisplayName("도서 검색어 조회 API 성공 테스트")
    void getSearchBookListTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AdminGetBookListResponseDto> bookList = new PageImpl<>(Collections.singletonList(
                new AdminGetBookListResponseDto(7L, new byte[0], "홍길동전", "미상", "꿈틀출판사",
                        "동화", "5세", List.of("영웅", "인물", "가족"), "ESFJ")));

        when(bookService.getSearchBookList("길동", pageable)).thenReturn(bookList);

        // bookList.getContent()를 통해 List로 변환 후 출력
        bookList.getContent().forEach(book -> System.out.println("도서 리스트: " + book));

        mockMvc.perform(get("/api/admin/books/search")
                        .param("search", "길동")
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content[0].title").value("홍길동전"));
    }

    @Test
    @DisplayName("도서 삭제 API 성공 테스트")
    void deleteBookTest() throws Exception {
        // Mock 서비스 계층에서 삭제 동작을 수행할 때 아무 일도 일어나지 않음을 설정
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/admin/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("book delete successfully"));
    }

    @Test
    @DisplayName("도서 수정 API 성공 테스트")
    void updateBookTest() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "book.jpg", MediaType.IMAGE_JPEG_VALUE, "image data".getBytes());
        AdminBookRequestDto book = new AdminBookRequestDto(
                "Updated Book",
                "Updated Author",
                "Updated Publisher",
                "2000",
                "200",
                "Adults",
                "Updated Summary",
                "ENTP",
                "Non-fiction",
                List.of("Philosophy")
        );

        String bookJson = objectMapper.writeValueAsString(book);

        mockMvc.perform(multipart("/api/admin/books/1")
                        .file(image)
                        .file(new MockMultipartFile("book", "", MediaType.APPLICATION_JSON_VALUE, bookJson.getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("book update successfully"));
    }
}
