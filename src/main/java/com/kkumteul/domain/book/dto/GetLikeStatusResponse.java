package com.kkumteul.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetLikeStatusResponse {
    private boolean isLiked;
    private boolean isDisliked;  // 추가된 필드
}