package com.kkumteul.domain.user.repository;

import com.kkumteul.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
