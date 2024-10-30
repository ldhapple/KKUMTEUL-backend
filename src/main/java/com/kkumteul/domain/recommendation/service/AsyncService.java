package com.kkumteul.domain.recommendation.service;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncService {

    private final TransactionalService transactionalService;

    @Async("asyncExecutor")
    public void updateLastActivity(Long childProfileId) {
        try {
            transactionalService.updateLastActivityTransactional(childProfileId);
        } catch (Exception e) {
            log.error("비동기 작업 중 오류 발생: {}", e.getMessage(), e);
        }
    }

}

