package com.kkumteul.domain.book.repository;

import com.kkumteul.domain.book.entity.BookMBTI;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookMBTIRepository extends JpaRepository<BookMBTI, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM BookMBTI bm WHERE bm.book.id = :bookId")
    void deleteByBookId(Long bookId);
}
