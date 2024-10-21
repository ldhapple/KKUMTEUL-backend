package com.kkumteul.domain.book.service;

import com.kkumteul.domain.book.dto.GetBookListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    // 전체 도서 목록 조회
    Page<GetBookListResponseDto> getBookList(Pageable pageable);

}
