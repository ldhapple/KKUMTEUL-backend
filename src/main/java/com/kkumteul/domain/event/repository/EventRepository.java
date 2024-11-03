package com.kkumteul.domain.event.repository;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e.id FROM Event e")
    Set<Long> findAllEventIds();

    Event findFirstByStartDateBeforeAndExpiredDateAfter(LocalDateTime now1, LocalDateTime now2);

    @Query(value = """
                SELECT e
                  FROM Event e
                 ORDER BY e.id ASC
            """)
    Page<Event> findAllEventInfo(final Pageable pageable);
}
