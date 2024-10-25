package com.kkumteul.domain.book.service;

import com.kkumteul.domain.book.dto.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public BookDto insertBook(AdminBookRequestDto adminInsertBookRequestDto, MultipartFile image){

        // 1.1. 이미지 처리 (MultipartFile -> byte[])
        byte[] bookImage;
        try {
            bookImage = image.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert image to bytes", e);
        }

        // 1.2. 장르 이름 >> 장르 객체 변환 (String -> Genre)
        Genre genre = genreService.getGenre(adminInsertBookRequestDto.getBookGenre());

        // 1.3. BookTopics 엔티티 생성
        List<BookTopic> bookTopics = new ArrayList<>();

        // 1.4. requestDto >> Book 엔티티 생성 및 저장
        Book book = Book.builder()
                .title(adminInsertBookRequestDto.getTitle())
                .author(adminInsertBookRequestDto.getAuthor())
                .publisher(adminInsertBookRequestDto.getPublisher())
                .price(adminInsertBookRequestDto.getPrice())
                .page(adminInsertBookRequestDto.getPage())
                .ageGroup(adminInsertBookRequestDto.getAgeGroup())
                .summary(adminInsertBookRequestDto.getSummary())
                .bookImage(bookImage)
                .genre(genre)
                .bookTopics(bookTopics)
                .build();

        // 1.5. Book 등록
        Book savedBook = bookRepository.save(book);

        // 1.6. BookTopics 에 요청한 Topic 데이터 저장
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

        // 1.7. BookMbti 엔티티 생성 및 저장
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

    // 2. 관리자의 전체 도서 목록 조회
    public Page<AdminGetBookListResponseDto> getAdminBookList(final Pageable pageable) {

        final Page<Book> books = bookRepository.findAllBookInfo(pageable);

        return books.map(AdminGetBookListResponseDto::fromEntity);
    }

    // 3. 관리자의 도서 상세 조회
    public AdminGetBookDetailResponseDto getBookDetailById(final Long bookId) {

        final Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId.toString()));

        return AdminGetBookDetailResponseDto.fromEntity(book);
    }

    // 4. 도서 수정
    @Transactional
    public BookDto updateBook(Long bookId, AdminBookRequestDto adminBookRequestDto, MultipartFile image){

        // 4.1. 도서 ID로 Book 엔티티 조회 (없는 경우에는 예외 처리)
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId.toString()));

        // 4.2. 이미지 갱신
        byte[] bookImage = book.getBookImage(); // 기존 이미지로 초기화
        if( image != null && !image.isEmpty()){
            try {
                bookImage = image.getBytes();
            } catch (IOException e) {
                throw new RuntimeException("Failed to convert image to bytes", e);
            }
        }

        // 4.3. 장르 갱신
        Genre updatedGenre = genreService.getGenre(adminBookRequestDto.getBookGenre());

        // 4.4. 주제어 갱신
        // 기존 Topics 삭제 후, 새로운 BookTopics 설정
        bookTopicService.deleteBookTopicByBookId(bookId);

        List<BookTopic> updatedBookTopics = new ArrayList<>();
        for (String topicName : adminBookRequestDto.getBookTopicList()){
            // 요청한 Topic 객체 가져오기
            Topic topic = topicService.getTopic(topicName);

            BookTopic bookTopic = BookTopic.builder()
                    .book(book)
                    .topic(topic)
                    .build();
            bookTopicService.insertBookTopic(bookTopic);
            updatedBookTopics.add(bookTopic);
        }

        // 4.5. mbti 갱신
        // 기존 mbti 삭제 후, 새로운 BookMBTI 설정
        bookMBTIService.deleteBookMBTIByBookId(bookId);
        // BookMbti 등록 전, 요청한 MBTI 객체 가져오기
        MBTI mbti = mbtiService.getMBTI(adminBookRequestDto.getBookMBTI());
        // BookMbti 등록
        BookMBTI updatedBookMbti = BookMBTI.builder()
                .book(book)
                .mbti(mbti)
                .build();
        bookMBTIService.insertBookMBTI(updatedBookMbti);

        // 4.6. 도서의 기본 데이터 갱신
        book.update(
                bookImage,
                adminBookRequestDto.getTitle(),
                adminBookRequestDto.getAuthor(),
                adminBookRequestDto.getPublisher(),
                adminBookRequestDto.getPrice(),
                adminBookRequestDto.getPage(),
                adminBookRequestDto.getAgeGroup(),
                adminBookRequestDto.getSummary(),
                updatedGenre,
                updatedBookTopics
                );

        bookRepository.save(book);

        return BookDto.fromEntity(book);
    }

    // 5. 도서 삭제
    public void deleteBook(Long bookId){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId.toString()));

        bookRepository.deleteById(bookId);
    }
}
