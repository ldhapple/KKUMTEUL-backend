package com.kkumteul.domain.book.repository;

import com.kkumteul.domain.book.entity.Book;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("전체 도서 반환: 요청 시 책 목록 Page 당 12개씩 반환한다.")
    void testFindAllBookInfo() {
        Pageable pageable = PageRequest.of(0, 12);
        Page<Book> result = bookRepository.findAllBookInfo(pageable);

        assertThat(result.getTotalElements()).isGreaterThan(0);
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("10대와 통하는 탈핵 이야기");
    }

    @Test
    @DisplayName("키워드로 도서 검색: 주어진 키워드에 해당하는 도서 목록을 반환한다.")
    void testFindBookListByKeyword() {
        String keyword = "환경";
        Pageable pageable = PageRequest.of(0, 12);
        Page<Book> result = bookRepository.findBookListByKeyword(keyword, pageable);

        assertThat(result.getTotalElements()).isGreaterThan(0);
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("내가 조금 불편하면 세상은 초록이 돼요 - 지구를 지키는 어린이들의 환경 실천법 50");
    }
}
