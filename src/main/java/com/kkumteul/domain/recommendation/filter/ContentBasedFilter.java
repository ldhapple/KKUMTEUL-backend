package com.kkumteul.domain.recommendation.filter;


import com.kkumteul.domain.recommendation.dto.BookDataDto;
import com.kkumteul.domain.recommendation.dto.ChildDataDto;
import com.kkumteul.domain.recommendation.dto.TopicDto;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
// 콘텐츠 기반 필터링
public class ContentBasedFilter {

    public Set<BookDataDto> filterBooksByUserPreferences(ChildDataDto childData, List<BookDataDto> booksData) {
        Set<BookDataDto> filteredBooks = new HashSet<>(); // 필터링 된 도서 리스트

        // 각 도서 필터링
        for (BookDataDto book : booksData) {
            double score = 0;

            // 장르 일치 시 점수 부여
            if (book.getGenreDto().getGenreName().equals(childData.getGenres().get(0).getGenreName())) {
                score += 0.5;
            }

            // 주제어 일치 시 점수 부여
            Set<String> childTopics = childData.getTopics().stream()
                    .map(TopicDto::getTopicName)
                    .collect(Collectors.toSet());

            for (TopicDto topic : book.getTopics()) {
                if (childTopics.contains(topic.getTopicName())) {
                    score += 0.3;
                }
            }

            // 점수를 BookDataDto에 추가
            book.addScore(score);

            if (score > 0) {
                filteredBooks.add(book);
            }
        }

        return filteredBooks;
    }
}
