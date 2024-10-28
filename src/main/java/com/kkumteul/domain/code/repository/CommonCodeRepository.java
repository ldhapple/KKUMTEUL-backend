package com.kkumteul.domain.code.repository;

import com.kkumteul.domain.code.Code;
import com.kkumteul.domain.code.key.CodeKey;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommonCodeRepository extends JpaRepository<Code, CodeKey> {

    @Query("SELECT c FROM Code c WHERE c.groupCode.groupCodeId = :groupCodeId")
    List<Code> findByGroupCodeId(Long groupCodeId);
}
