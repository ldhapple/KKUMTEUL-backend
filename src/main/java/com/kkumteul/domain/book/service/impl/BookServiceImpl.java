package com.kkumteul.domain.book.service.impl;

import com.kkumteul.domain.book.dto.GetBookListResponse;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.repository.BookRepository;
import com.kkumteul.domain.book.repository.BookTopicRepository;
import com.kkumteul.domain.book.service.BookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookTopicRepository bookTopicRepository;

    public List<GetBookListResponse> getBookList() {
        List<Book> books = bookRepository.findAll(); // 모든 책을 가져옴

        return books.stream()
                .map(book -> {
                    List<String> topics = bookTopicRepository.findByBook(book).stream()
                            .map(bookTopic -> bookTopic.getTopic().getName())
                            .collect(Collectors.toList());

                    return GetBookListResponse.builder()
                            .bookId(book.getId())
                            .title(book.getTitle())
                            .bookImage(book.getBookImage())
                            .topics(topics)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
