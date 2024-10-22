package com.kkumteul.domain.book.controller;

import com.kkumteul.domain.book.dto.AdminInsertBookRequestDto;
import com.kkumteul.domain.book.dto.BookDto;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookGenre;
import com.kkumteul.domain.book.entity.BookMBTI;
import com.kkumteul.domain.book.entity.BookTopic;
import com.kkumteul.domain.book.service.BookGenreService;
import com.kkumteul.domain.book.service.BookMBTIService;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.domain.book.service.BookTopicService;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.service.MBTIService;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.personality.service.GenreService;
import com.kkumteul.domain.personality.service.TopicService;
import com.kkumteul.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.kkumteul.util.ApiUtil.ApiSuccess;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")
public class AdminBookController {

    private final BookService bookService;
    private final BookGenreService bookGenreService;
    private final BookMBTIService bookMBTIService;
    private final BookTopicService bookTopicService;

    private final GenreService genreService;
    private final MBTIService mbtiService;
    private final TopicService topicService;

    // 1. 도서 등록
    @PostMapping
    public ApiSuccess<?> insertBook(@RequestBody AdminInsertBookRequestDto adminInsertBookRequestDto) {
        // 1.1. Book 엔티티 생성 및 저장
        Book book = new Book(
                adminInsertBookRequestDto.getTitle(),
                adminInsertBookRequestDto.getAuthor(),
                adminInsertBookRequestDto.getPublisher(),
                adminInsertBookRequestDto.getPrice(),
                adminInsertBookRequestDto.getPage(),
                adminInsertBookRequestDto.getSummary(),
                adminInsertBookRequestDto.getAgeGroup(),
                adminInsertBookRequestDto.getBookImage()
        );

        // 1.2. 도서 등록
        BookDto savedBook = bookService.insertBook(book);
        // 1.3. 저장된 도서의 ID 가져오기
        Long savedBookId = bookService.getBookById(savedBook.getId()).getId();

        // 1.4. BookGenre 엔티티 생성 및 저장
        for (Genre genre : adminInsertBookRequestDto.getBookGenreList()) {
            // bookGenre 등록 전, Genre 먼저 등록
            Genre savedGenre = genreService.insertGenre(genre);

            // bookGenre 등록
            BookGenre bookGenre = BookGenre.builder()
                    .book(book)
                    .genre(savedGenre)
                    .build();
            bookGenreService.insertBookGenre(bookGenre);
        }

        // 1.5. BookMbti 엔티티 생성 및 저장
        {
            // BookMbti 등록 전, MBTI 먼저 등록
            MBTI savedMBTI = mbtiService.insertMBTI(adminInsertBookRequestDto.getBookMbti());

            // BookMbti 등록
            BookMBTI bookMbti = BookMBTI.builder()
                    .book(book)
                    .mbti(savedMBTI)
                    .build();
            bookMBTIService.insertBookMBTI(bookMbti);
        }

        // 1.6. BookTopic 엔티티 생성 및 저장
        for (Topic topic : adminInsertBookRequestDto.getBookTopicList()) {
            // BookTopic 등록 전, Topic 먼저 등록
            Topic savedTopic = topicService.insertTopic(topic);

            // BookTopic 등록
            BookTopic bookTopic = BookTopic.builder()
                    .book(book)
                    .topic(savedTopic)
                    .build();
            bookTopicService.insertBookTopic(bookTopic);
        }

        return ApiUtil.success("book insert successfully");
    }

}
