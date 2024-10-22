package com.kkumteul.domain.book.service;

import com.kkumteul.domain.book.dto.AdminInsertBookRequestDto;
import com.kkumteul.domain.book.dto.BookDto;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookGenre;
import com.kkumteul.domain.book.entity.BookMBTI;
import com.kkumteul.domain.book.entity.BookTopic;
import com.kkumteul.domain.book.repository.BookRepository;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.service.MBTIService;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.personality.service.GenreService;
import com.kkumteul.domain.personality.service.TopicService;
import com.kkumteul.exception.BookNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final BookGenreService bookGenreService;
    private final BookMBTIService bookMBTIService;
    private final BookTopicService bookTopicService;

    private final GenreService genreService;
    private final MBTIService mbtiService;
    private final TopicService topicService;

    // 1. 도서 등록
    public BookDto insertBook(AdminInsertBookRequestDto adminInsertBookRequestDto){
        // 1.1. Book 엔티티 생성 및 저장
        Book book = Book.builder()
                .title(adminInsertBookRequestDto.getTitle())
                .author(adminInsertBookRequestDto.getAuthor())
                .publisher(adminInsertBookRequestDto.getPublisher())
                .price(adminInsertBookRequestDto.getPrice())
                .page(adminInsertBookRequestDto.getPage())
                .summary(adminInsertBookRequestDto.getSummary())
                .ageGroup(adminInsertBookRequestDto.getAgeGroup())
                .bookImage(adminInsertBookRequestDto.getBookImage())
                .build();

        // 1.2. 도서 등록
        Book savedBook = bookRepository.save(book);

        // 1.3. BookGenre 엔티티 생성 및 저장
        for (Genre genre : adminInsertBookRequestDto.getBookGenreList()) {
            // bookGenre 등록 전, Genre 먼저 등록
            Genre savedGenre = genreService.insertGenre(genre);
            // bookGenre 등록
            BookGenre bookGenre = BookGenre.builder()
                    .book(savedBook)
                    .genre(savedGenre)
                    .build();
            bookGenreService.insertBookGenre(bookGenre);
        }

        // 1.4. BookMbti 엔티티 생성 및 저장
        {
            // BookMbti 등록 전, MBTI 먼저 등록
            MBTI savedMBTI = mbtiService.insertMBTI(adminInsertBookRequestDto.getBookMbti());
            // BookMbti 등록
            BookMBTI bookMbti = BookMBTI.builder()
                    .book(savedBook)
                    .mbti(savedMBTI)
                    .build();
            bookMBTIService.insertBookMBTI(bookMbti);
        }

        // 1.5. BookTopic 엔티티 생성 및 저장
        for (Topic topic : adminInsertBookRequestDto.getBookTopicList()) {
            // BookTopic 등록 전, Topic 먼저 등록
            Topic savedTopic = topicService.insertTopic(topic);
            // BookTopic 등록
            BookTopic bookTopic = BookTopic.builder()
                    .book(savedBook)
                    .topic(savedTopic)
                    .build();
            bookTopicService.insertBookTopic(bookTopic);
        }

        return BookDto.fromEntity(savedBook);
    }

    // 2. 도서 조회
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        BookDto getBookDto = BookDto.fromEntity(book);

        return getBookDto;
    }
}
