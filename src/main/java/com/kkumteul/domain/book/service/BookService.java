package com.kkumteul.domain.book.service;

import com.kkumteul.domain.book.dto.GetBookListResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {

    // 전체 도서 목록 조회
    List<GetBookListResponse> getBookList();

}
