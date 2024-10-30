package com.kkumteul.domain.code.service;

import com.kkumteul.domain.code.dto.CodeDto;
import com.kkumteul.domain.code.repository.CommonCodeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonCodeService {

    private final CommonCodeRepository commonCodeRepository;

    public List<CodeDto> getCodesByGroupCode(Long groupCodeId) {
        log.info("get Codes by GroupCode - Input GroupCodeId: {}", groupCodeId);

        return commonCodeRepository.findByGroupCodeId(groupCodeId).stream()
                .map(CodeDto::fromEntity)
                .toList();
    }
}
