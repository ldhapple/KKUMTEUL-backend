package com.kkumteul.domain.book.repository;

import com.kkumteul.domain.book.entity.BookMBTI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookMBTIRepository extends JpaRepository<BookMBTI, Long> {
}
