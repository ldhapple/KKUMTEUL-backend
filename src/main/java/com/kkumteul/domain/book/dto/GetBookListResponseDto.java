package com.kkumteul.domain.book.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetBookListResponseDto {
    private String title;
    private byte[] bookImage; // 또는 String으로 바꿔서 이미지 URL을 저장할 수도 있습니다.
    private List<String> topics; // Topic 이름 리스트
}

