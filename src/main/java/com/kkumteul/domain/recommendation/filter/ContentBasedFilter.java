package com.kkumteul.domain.recommendation.filter;


import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookTopic;
import com.kkumteul.domain.book.repository.BookLikeRepository;
import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.recommendation.dto.BookDataDto;
import com.kkumteul.domain.recommendation.dto.ChildDataDto;
import com.kkumteul.domain.recommendation.dto.GenreDto;
import com.kkumteul.domain.recommendation.dto.TopicDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
// 콘텐츠 기반 필터링
public class ContentBasedFilter {

    private final SimilarityCalculator similarityCalculator;
    private final BookLikeRepository likeRepository;

    public Map<BookDataDto, Double> filterBooksByUserPreferences(ChildDataDto childData,
                                                                 List<BookDataDto> booksData) {

        Map<BookDataDto, Double> contentScores = new HashMap<>();

        // 1. 사용자가 좋아요한 도서 가져오기
        List<Book> likedBooks = likeRepository.findLikedBooksByUser(childData.getId());

//        log.info("사용자(target)가 좋아요한 도서: {}", likedBooks.toString());

        // 2. 좋아요한 도서에서 장르 및 주제 추출
        Set<String> userGenres = new HashSet<>();
        Set<String> userTopics = new HashSet<>();

        for (Book likedBook : likedBooks) {
            userGenres.add(likedBook.getGenre().getName());

            for (BookTopic topic : likedBook.getBookTopics()) {
                userTopics.add(topic.getTopic().getName());
            }
//            log.info("사용자(target)가 좋아요 한 도서의 장르: " + userGenres.toString());
//            log.info("사용자(target)가 좋아요 한 도서의 주제어: " + userTopics.toString());

        }

        // 3. 사용자가 직접 선호한 장르와 주제 병합
        for (GenreDto genre : childData.getGenres()) {
            userGenres.add(genre.getGenreName());
        }
        for (TopicDto topic : childData.getTopics()) {
            userTopics.add(topic.getTopicName());
        }

//        log.info("사용자 선호 장르 + 좋아요 장르: {} | 사용자 선호 주제어 + 좋아요 주제어: {}", userGenres, userTopics);

        // 4. 각 도서에 대해 장르 및 주제어 유사도 계산
        for (BookDataDto book : booksData) {
            String bookGenre = book.getGenreDto().getGenreName();

            List<String> bookTopics = new ArrayList<>();
            for (TopicDto topic : book.getTopics()) {
                bookTopics.add(topic.getTopicName());
            }

            // 장르 및 주제어 유사도 계산
            double genreSimilarity = similarityCalculator.cosineSimilarity(
                    new ArrayList<>(userGenres), List.of(bookGenre));
            double topicSimilarity = similarityCalculator.cosineSimilarity(
                    new ArrayList<>(userTopics), bookTopics);

            // 최종 점수 계산 (장르: 40%, 주제어: 60%)
            double totalScore = (genreSimilarity * 0.4) + (topicSimilarity * 0.6);

//            log.info("도서: {} | 장르 유사도: {} | 주제 유사도: {} | 최종 점수: {}",
//                    book.getTitle(), genreSimilarity, topicSimilarity, totalScore);

            // 점수가 0 이상인 도서만 결과에 추가
            if (totalScore > 0) {
                contentScores.put(book, totalScore);
            }
        }

        return contentScores;
    }
}
