package com.kkumteul.domain.childprofile.dto;

import com.kkumteul.domain.book.dto.BookLikeDto;
import com.kkumteul.domain.history.dto.ChildPersonalityHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public class ChildProfileResponseDto {
    private String childName;
    private List<BookLikeDto> bookLikeList;
    private List<ChildPersonalityHistoryDto> childPersonalityHistoryList;

}
