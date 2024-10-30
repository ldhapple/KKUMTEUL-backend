package com.kkumteul.domain.user.repository;

import com.kkumteul.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByNickName(String nickName); // 닉네임 중복 여부 확인
    boolean existsByUsername(String username); // 아이디 중복 여부 확인
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.childProfileList WHERE u.id = :userId")
    Optional<User> findByIdWithChildProfiles(Long userId);
}