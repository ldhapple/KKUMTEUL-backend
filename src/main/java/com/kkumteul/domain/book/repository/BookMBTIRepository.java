package com.kkumteul.domain.book.repository;

import com.kkumteul.domain.book.entity.BookMBTI;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookMBTIRepository extends JpaRepository<BookMBTI, Long> {
    @Transactional
    void deleteByBookId(Long bookId);
}
