package com.kkumteul.domain.book.service.impl;

import static com.kkumteul.util.kafka.KafkaTopic.BOOK_DISLIKE;
import static com.kkumteul.util.kafka.KafkaTopic.BOOK_LIKE;

import com.kkumteul.domain.book.dto.GetBookDetailResponseDto;
import com.kkumteul.domain.book.dto.GetBookListResponseDto;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookLike;
import com.kkumteul.domain.book.entity.LikeType;
import com.kkumteul.domain.book.exception.BookNotFoundException;
import com.kkumteul.domain.book.repository.BookLikeRepository;
import com.kkumteul.domain.book.repository.BookRepository;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.domain.personality.repository.TopicRepository;
import com.kkumteul.exception.EntityNotFoundException;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.util.kafka.KafkaUtil;
import jakarta.persistence.Tuple;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookLikeRepository bookLikeRepository;
    private final ChildProfileRepository childProfileRepository;
    private final TopicRepository topicRepository;
    private final KafkaUtil kafkaUtil;

    // 전체 도서 목록 조회
    @Override
    @Transactional(readOnly = true)
    public Page<GetBookListResponseDto> getBookList(final Pageable pageable) {
        final Page<Book> books = bookRepository.findAllBookInfo(pageable);

        return books.map(GetBookListResponseDto::from);
    }

    @Override
    @Cacheable(value = "book", key = "#bookId")
    @Transactional(readOnly = true)
    public Book getBookWithCache(Long bookId) {
        log.info("get Book - bookID: {}", bookId);
        return bookRepository.findBookByIdWithGenreAndTopic(bookId)
                .orElseThrow(() -> new EntityNotFoundException(bookId));
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBook(Long bookId) {
        log.info("get Book - bookID: {}", bookId);
        return bookRepository.findBookByIdWithGenreAndTopic(bookId)
                .orElseThrow(() -> new EntityNotFoundException(bookId));
    }

    // 검색 키워드 추출된 도서 목록 조회
    @Override
    public Page<GetBookListResponseDto> getBookList(final String keyword, final Pageable pageable) {
//        final Page<Book> books = bookRepository.findBookListByKeyword(keyword, pageable);
//        Page<Tuple> books = bookRepository.findBookListByKeyword(keyword, pageable);
//        return books.map(GetBookListResponseDto::create);
        List<String> validTopics = topicRepository.findAllTopicNames();
        Set<String> topics = new HashSet<>(validTopics);

        Page<Tuple> results = bookRepository.findBookListByKeyword(keyword, pageable);

        return results.map(result -> {
            List<String> topicNames = extractTopics(result.get("topicNames", String.class), topics);
            return GetBookListResponseDto.create(result, topicNames);
        });
//        return books.map(GetBookListResponseDto::from);
    }

    // 상세 도서 조회
    @Override
    public GetBookDetailResponseDto getBookDetail(final Long bookId) {

        final Book book = bookRepository.findById(bookId)
                .orElseThrow(BookNotFoundException::new);

        return GetBookDetailResponseDto.from(book);
    }

    // 좋아요, 싫어요 처리
    @Override
    @Transactional
    public void bookLike(final Long bookId, final Long childProfileId, final LikeType likeType) {
        final Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("책을 찾을 수 없습니다."));

        String message = childProfileId + ":" + bookId + ":" + likeType.name();

        // 1. 도서에 좋아요/싫어요 버튼 상태 확인
        Optional<BookLike> existLike = bookLikeRepository.findByChildProfileAndBook(childProfileId, bookId);

        if (existLike.isPresent()) {
            // 2-1. 이미 있는 상태 확인
            BookLike bookLike = existLike.get();

            if (bookLike.getLikeType() == likeType) {
                throw new RuntimeException(likeType == LikeType.LIKE ? "이미 좋아요를 눌렀습니다." : "이미 싫어요를 눌렀습니다.");
            } else {
                // 2-2. 상태 변경
                bookLike.updateLikeType(likeType);
                bookLike.updateUpdateAt();

                bookLikeRepository.save(bookLike);

                kafkaUtil.sendMessage(
                        likeType == LikeType.LIKE ? BOOK_LIKE.getTopicName() : BOOK_DISLIKE.getTopicName(), message);
                log.info("Kafka message sent - childProfileId: {}, bookId: {}, likeType: {}", childProfileId, bookId,
                        likeType);
            }
        } else {
            // 3. 처음 누를 때
            BookLike booklike = BookLike.builder()
                    .book(book)
                    .childProfile(childProfileRepository.findById(childProfileId)
                            .orElseThrow(() -> new RuntimeException("자녀를 찾을 수 없습니다.")))
                    .likeType(likeType)
                    .updatedAt(LocalDateTime.now())
                    .build();

            bookLikeRepository.save(booklike);

            kafkaUtil.sendMessage(likeType == LikeType.LIKE ? BOOK_LIKE.getTopicName() : BOOK_DISLIKE.getTopicName(),
                    message);
            log.info("Kafka message sent - childProfileId: {}, bookId: {}, likeType: {}", childProfileId, bookId,
                    likeType);
        }
    }

    @Override
    public Map<String, Boolean> checkLikeStatus(Long bookId, Long childProfileId) {
        Map<String, Boolean> likeStatus = new HashMap<>();

        // 좋아요 상태 확인
        boolean isLiked = bookLikeRepository.existsByBookIdAndChildProfileIdAndLikeType(bookId, childProfileId,
                LikeType.LIKE);
        likeStatus.put("isLiked", isLiked);

        // 싫어요 상태 확인
        boolean isDisliked = bookLikeRepository.existsByBookIdAndChildProfileIdAndLikeType(bookId, childProfileId,
                LikeType.DISLIKE);
        likeStatus.put("isDisliked", isDisliked);

        return likeStatus;
    }

    private List<String> extractTopics(String searchText, Set<String> topics) {
        if (searchText == null || searchText.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(searchText.split(" "))
                .filter(topics::contains)
                .collect(Collectors.toList());
    }
}
