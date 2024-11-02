package com.kkumteul.domain.event.repository;

import com.kkumteul.domain.event.entity.Event;
import com.kkumteul.domain.event.entity.JoinEvent;
import com.kkumteul.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface JoinEventRepository extends JpaRepository<JoinEvent, Long> {
//    boolean existsByUserAndEvent(User user, Event event);
    @Query("SELECT j FROM JoinEvent j WHERE j.createdAt >= :startOfHour AND j.createdAt < :endOfHour")
    List<JoinEvent> findEventsAroundOnePM(@Param("startOfHour") LocalDateTime startOfHour, @Param("endOfHour") LocalDateTime endOfHour);
}
