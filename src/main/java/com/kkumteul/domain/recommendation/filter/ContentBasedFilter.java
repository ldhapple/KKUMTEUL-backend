package com.kkumteul.domain.recommendation.filter;


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

    public Map<BookDataDto, Double> filterBooksByUserPreferences(ChildDataDto childData, List<BookDataDto> booksData) {
        Map<BookDataDto, Double> contentScores = new HashMap<>();

        // 사용자의 선호 장르 및 주제어를 리스트로 가져오기
        List<String> userGenres = new ArrayList<>();
        for (GenreDto genre : childData.getGenres()) {
            userGenres.add(genre.getGenreName());
        }

        List<String> userTopics = new ArrayList<>();
        for (TopicDto topic : childData.getTopics()) {
            userTopics.add(topic.getTopicName());
        }

        // 각 도서에 대해 장르 및 주제어 유사도 계산
        for (BookDataDto book : booksData) {
            // 도서의 장르와 주제어 가져오기
            String bookGenre = book.getGenreDto().getGenreName();

            List<String> bookTopics = new ArrayList<>();
            for (TopicDto topic : book.getTopics()) {
                bookTopics.add(topic.getTopicName());
            }

            // 코사인 유사도 계산(장르와 토픽)
            double genreSimilarity = similarityCalculator.cosineSimilarity(userGenres, List.of(bookGenre));
            double topicSimilarity = similarityCalculator.cosineSimilarity(userTopics, bookTopics);


            // 최종 점수 계산 (장르: 60%, 주제어: 40%)
            double totalScore = (genreSimilarity * 0.6) + (topicSimilarity * 0.4);

            // 점수가 0 이상인 도서만 결과에 추가
            if (totalScore > 0) {
                contentScores.put(book, totalScore);
            }
        }

        return contentScores;
    }
}
