package com.kkumteul.domain.recommendation.service;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionalService {

    private final ChildProfileRepository childProfileRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateLastActivityTransactional(Long childProfileId) {

        ChildProfile childProfile = entityManager.find(ChildProfile.class, childProfileId);
        if (childProfile == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        entityManager.joinTransaction(); // 현재 실행 중인 트랜잭션에 해당 EntityManager를 수동으로 연결 - flush가 올바른 트랜잭션에서 수행하기 위해

        childProfile.updateLastActivity();  // lastActivity 필드 업데이트

        entityManager.merge(childProfile);  // 엔터티를 병합

        entityManager.flush();  // 직접 플러시 호출

    }
}
