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

//        log.info("사용자(target)가 좋아요한 도서: {}", likedBooks);

        // 2. 좋아요한 도서에서 장르 및 주제 추출
        Set<String> likedGenres = new HashSet<>();
        Set<String> likedTopics = new HashSet<>();

        for (Book likedBook : likedBooks) {
            likedGenres.add(likedBook.getGenre().getName());

            for (BookTopic topic : likedBook.getBookTopics()) {
                likedTopics.add(topic.getTopic().getName());
            }
        }

//        log.info("좋아요한 장르: {} | 좋아요한 주제: {}", likedGenres, likedTopics);

        // 3. 사용자가 직접 선호한 장르와 주제 추출
        Set<String> preferredGenres = childData.getGenres().stream()
                .map(GenreDto::getGenreName)
                .collect(Collectors.toSet());

        Set<String> preferredTopics = childData.getTopics().stream()
                .map(TopicDto::getTopicName)
                .collect(Collectors.toSet());

//        log.info("사용자 선호 장르: {} | 사용자 선호 주제: {}", preferredGenres, preferredTopics);

        // 4. 각 도서에 대해 장르 및 주제어 유사도 계산
        for (BookDataDto book : booksData) {
            String bookGenre = book.getGenreDto().getGenreName();
            List<String> bookTopics = book.getTopics().stream()
                    .map(TopicDto::getTopicName)
                    .collect(Collectors.toList());

            // 5. 좋아요한 장르와 주제에 대한 유사도 계산
            double likedGenreSimilarity = similarityCalculator.cosineSimilarity(
                    new ArrayList<>(likedGenres), List.of(bookGenre));
            double likedTopicSimilarity = similarityCalculator.cosineSimilarity(
                    new ArrayList<>(likedTopics), bookTopics);

            // 6. 사용자가 직접 선호한 장르와 주제에 대한 유사도 계산
            double preferredGenreSimilarity = similarityCalculator.cosineSimilarity(
                    new ArrayList<>(preferredGenres), List.of(bookGenre));
            double preferredTopicSimilarity = similarityCalculator.cosineSimilarity(
                    new ArrayList<>(preferredTopics), bookTopics);

            // 7. 각 유사도에 가중치 부여 (좋아요: 30%, 직접 선호: 70%)
            double genreSimilarity = (likedGenreSimilarity * 0.3) + (preferredGenreSimilarity * 0.7);
            double topicSimilarity = (likedTopicSimilarity * 0.3) + (preferredTopicSimilarity * 0.7);

            // 8. 사용자와 도서의 MBTI 유사도 계산
            double mbtiSimilarity = similarityCalculator.calculateMbtiSimilarity(
                    childData.getMbti().name(), book.getMbti().get(0).name());

            // 9. 최종 점수 계산 (장르: 30%, 주제: 40%, MBTI: 30%)
            double totalScore = (genreSimilarity * 0.3) +
                    (topicSimilarity * 0.4) +
                    (mbtiSimilarity * 0.3);

//            log.info("도서: {} | 장르 유사도: {} | 주제 유사도: {} | MBTI 유사도: {} | 최종 점수: {}",
//                    book.getTitle(), genreSimilarity, topicSimilarity, mbtiSimilarity, totalScore);

            // 10. 점수가 0 이상인 도서만 결과에 추가
            if (totalScore > 0) {
                contentScores.put(book, totalScore);
            }
        }

        return contentScores;
    }

}
