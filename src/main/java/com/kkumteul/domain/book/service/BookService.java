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

        // 1.0. requestDto 필드 값이 null인지 검증
        validateRequestDto(adminInsertBookRequestDto);

        // 1.1. 이미지 처리 (MultipartFile -> byte[])
        byte[] bookImage = processImage(image);

        // 1.2. 장르 이름 >> 장르 객체 변환 (String -> Genre)
        Genre genre = genreService.getGenre(adminInsertBookRequestDto.getBookGenre());
        if( genre == null ){
            throw new IllegalArgumentException("Genre can't be null");
        }

        // 1.3. requestDto >> Book 엔티티 생성 및 저장
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
                .build();

        // 1.4. Book 등록
        Book savedBook = bookRepository.save(book);

        // 1.5. BookTopics 데이터 등록
        createBookTopics(savedBook, adminInsertBookRequestDto.getBookTopicList());

        // 1.6. BookMbti 데이터 등록
        createBookMBTI(savedBook, adminInsertBookRequestDto.getBookMBTI());

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

        // 4.0. requestDto 필드 값이 null인지 검증
        validateRequestDto(adminBookRequestDto);

        // 4.1. 도서 ID로 Book 엔티티 조회 (없는 경우에는 예외 처리)
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId.toString()));

        // 4.2. 이미지 갱신
        byte[] bookImage = processImage(image);

        // 4.3. 장르 갱신
        Genre updatedGenre = genreService.getGenre(adminBookRequestDto.getBookGenre());
        if( updatedGenre == null ){
            throw new IllegalArgumentException("Genre can't be null");
        }

        // 4.4. 주제어 갱신
        // 기존 Topics 삭제 후, 새로운 BookTopics로 갱신
        bookTopicService.deleteBookTopicByBookId(bookId);
        createBookTopics(book, adminBookRequestDto.getBookTopicList());

        // 4.5. mbti 갱신
        // 기존 mbti 삭제 후, 새로운 BookMBTI로 갱신
        bookMBTIService.deleteBookMBTIByBookId(bookId);
        createBookMBTI(book, adminBookRequestDto.getBookMBTI());

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
                updatedGenre
                );

        return BookDto.fromEntity(book);
    }

    // 5. 도서 삭제
    public void deleteBook(Long bookId){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId.toString()));

        bookRepository.deleteById(bookId);
    }

    // 이미지 처리
    private byte[] processImage(MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            try {
                return image.getBytes();
            } catch (IOException e) {
                throw new RuntimeException("Failed to convert image to bytes", e);
            }
        }
        return new byte[0]; // 기본값 설정 (또는 기본 이미지)
    }

    // BookTopic 생성 헬퍼 메서드
    private void createBookTopics(Book book, List<String> topicNames) {
        for (String topicName : topicNames) {
            Topic topic = topicService.getTopic(topicName);

            BookTopic bookTopic = BookTopic.builder()
                    .book(book)
                    .topic(topic)
                    .build();
            bookTopicService.insertBookTopic(bookTopic);
        }
    }

    // BookMBTI 생성 헬퍼 메서드
    private void createBookMBTI(Book book, String mbtiName) {
        MBTI mbti = mbtiService.getMBTI(mbtiName);

        // BookMbti 등록
        BookMBTI bookMbti = BookMBTI.builder()
                .book(book)
                .mbti(mbti)
                .build();
        bookMBTIService.insertBookMBTI(bookMbti);
    }

    // requestDto가 null인지 검증
    private void validateRequestDto(AdminBookRequestDto requestDto) {
        if (requestDto == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (requestDto.getTitle() == null || requestDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        if (requestDto.getAuthor() == null || requestDto.getAuthor().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty");
        }

        if (requestDto.getPublisher() == null || requestDto.getPublisher().isEmpty()) {
            throw new IllegalArgumentException("Publisher cannot be null or empty");
        }

        if (requestDto.getPrice() == null || requestDto.getPrice().isEmpty()) {
            throw new IllegalArgumentException("Price cannot be null or empty");
        }

        if (requestDto.getPage() == null || requestDto.getPage().isEmpty()) {
            throw new IllegalArgumentException("Page cannot be null or empty");
        }

        if (requestDto.getAgeGroup() == null || requestDto.getAgeGroup().isEmpty()) {
            throw new IllegalArgumentException("Age group cannot be null or empty");
        }

        if (requestDto.getSummary() == null || requestDto.getSummary().isEmpty()) {
            throw new IllegalArgumentException("Summary cannot be null or empty");
        }

        if (requestDto.getBookGenre() == null || requestDto.getBookGenre().isEmpty()) {
            throw new IllegalArgumentException("Genre cannot be null or empty");
        }

        if (requestDto.getBookTopicList() == null || requestDto.getBookTopicList().isEmpty()) {
            throw new IllegalArgumentException("Book topics cannot be null or empty");
        }

        if (requestDto.getBookMBTI() == null || requestDto.getBookMBTI().isEmpty()) {
            throw new IllegalArgumentException("Book MBTI cannot be null or empty");
        }
    }

}
