package com.kkumteul.domain.book.service;

import com.kkumteul.domain.book.dto.AdminInsertBookRequestDto;
import com.kkumteul.domain.book.dto.BookDto;
import com.kkumteul.domain.book.entity.Book;
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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final BookMBTIService bookMBTIService;
    private final BookTopicService bookTopicService;

    private final GenreService genreService;
    private final MBTIService mbtiService;
    private final TopicService topicService;

    // 1. 도서 등록
    public BookDto insertBook(AdminInsertBookRequestDto adminInsertBookRequestDto){

        // 1.1. 장르 이름 >> 장르 객체 변환 (String -> Genre)
        Genre genre = genreService.getGenre(adminInsertBookRequestDto.getBookGenre());

        // 1.2. BookTopics 엔티티 생성
        List<BookTopic> bookTopics = new ArrayList<>();

        // 1.3. requestDto >> Book 엔티티 생성 및 저장
        Book book = Book.builder()
                .title(adminInsertBookRequestDto.getTitle())
                .author(adminInsertBookRequestDto.getAuthor())
                .publisher(adminInsertBookRequestDto.getPublisher())
                .price(adminInsertBookRequestDto.getPrice())
                .page(adminInsertBookRequestDto.getPage())
                .ageGroup(adminInsertBookRequestDto.getAgeGroup())
                .summary(adminInsertBookRequestDto.getSummary())
                .bookImage(adminInsertBookRequestDto.getBookImage())
                .genre(genre)
                .bookTopics(bookTopics)
                .build();

        // 1.4. Book 등록
        Book savedBook = bookRepository.save(book);

        // 1.5. BookTopics 에 요청한 Topic 데이터 저장
        for (String topicName : adminInsertBookRequestDto.getBookTopicList()){
            // 요청한 Topic 객체 가져오기
            Topic topic = topicService.getTopic(topicName);

            BookTopic bookTopic = BookTopic.builder()
                    .book(savedBook)
                    .topic(topic)
                    .build();

            bookTopicService.insertBookTopic(bookTopic);
            bookTopics.add(bookTopic);
        }

        // 1.6. BookMbti 엔티티 생성 및 저장
        {
            // BookMbti 등록 전, 요청한 MBTI 객체 가져오기
            MBTI mbti = mbtiService.getMBTI(adminInsertBookRequestDto.getBookMBTI());

            // BookMbti 등록
            BookMBTI bookMbti = BookMBTI.builder()
                    .book(savedBook)
                    .mbti(mbti)
                    .build();
            bookMBTIService.insertBookMBTI(bookMbti);
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
