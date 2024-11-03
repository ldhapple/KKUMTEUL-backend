package com.kkumteul.domain.recommendation.dto;

import com.kkumteul.domain.book.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResultDto {
    private Long userId;
    private List<Book> books;

}