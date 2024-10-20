package com.kkumteul.domain.code.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.kkumteul.domain.code.Code;
import com.kkumteul.domain.code.GroupCode;
import com.kkumteul.domain.code.dto.CodeDto;
import com.kkumteul.domain.code.key.CodeKey;
import com.kkumteul.domain.code.repository.CommonCodeRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonCodeServiceTest {

    @Mock
    private CommonCodeRepository commonCodeRepository;

    @InjectMocks
    private CommonCodeService commonCodeService;

    @Test
    @DisplayName("그룹 코드로 코드 목록 가져오기 테스트")
    void testGetCodes() {
        GroupCode groupCode = new GroupCode();
        groupCode.setName("장르");
        Long groupCodeId = 1L;

        Code code1 = new Code();
        code1.setId(new CodeKey(groupCodeId, 1L));
        code1.setCodeName("그림책");
        code1.setOrderNo(1);
        code1.setGroupCode(groupCode);


        Code code2 = new Code();
        code2.setId(new CodeKey(groupCodeId, 2L));
        code2.setCodeName("만화");
        code2.setOrderNo(2);
        code2.setGroupCode(groupCode);

        given(commonCodeRepository.findByGroupCodeId(groupCodeId)).willReturn(List.of(code1, code2));

        List<CodeDto> codes = commonCodeService.getCodesByGroupCode(groupCodeId);

        assertThat(codes).isNotNull();
        assertThat(codes).hasSize(2);
        assertThat(codes.get(1).getCodeName()).isEqualTo("만화");
    }
}