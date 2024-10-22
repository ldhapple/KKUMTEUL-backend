package com.kkumteul.domain.code.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.kkumteul.domain.code.Code;
import com.kkumteul.domain.code.GroupCode;
import com.kkumteul.domain.code.key.CodeKey;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CommonCodeRepositoryTest {

    @Autowired
    private CommonCodeRepository commonCodeRepository;

    @Autowired
    private EntityManager entityManager;

    private GroupCode groupCode;

    @BeforeEach
    void setUp() {
        groupCode = new GroupCode();
        groupCode.setName("장르");
        entityManager.persist(groupCode);

        Code code1 = new Code();
        code1.setId(new CodeKey(groupCode.getGroupCodeId(), 1L));
        code1.setCodeName("그림책");
        code1.setOrderNo(1);
        code1.setGroupCode(groupCode);


        Code code2 = new Code();
        code2.setId(new CodeKey(groupCode.getGroupCodeId(), 2L));
        code2.setCodeName("만화");
        code2.setOrderNo(2);
        code2.setGroupCode(groupCode);

        entityManager.persist(code1);
        entityManager.persist(code2);
    }

    @Test
    @DisplayName("그룹 코드 ID로 코드 목록 조회 테스트")
    void testGetCodes() {
        List<Code> codes = commonCodeRepository.findByGroupCodeId(groupCode.getGroupCodeId());

        assertThat(codes).hasSize(2);
        assertThat(codes.get(0).getCodeName()).isEqualTo("그림책");
    }
}