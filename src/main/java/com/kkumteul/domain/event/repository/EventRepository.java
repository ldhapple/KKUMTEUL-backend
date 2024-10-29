package com.kkumteul.domain.event.repository;

import com.kkumteul.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e.id FROM Event e")
    Set<Long> findAllEventIds();
}
