package com.kkumteul.domain.code.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kkumteul.domain.code.dto.CodeDto;
import com.kkumteul.domain.code.service.CommonCodeService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(CommonCodeController.class)
class CommonCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommonCodeService commonCodeService;

    @Test
    @WithMockUser("user")
    @DisplayName("공통 코드 조회 API 성공 테스트")
    void testGetCodesSuccess() throws Exception {
        Long groupCodeId = 1L;

        List<CodeDto> codes = List.of(
                new CodeDto(1L, "그림책"),
                new CodeDto(2L, "만화")
        );

        given(commonCodeService.getCodesByGroupCode(groupCodeId)).willReturn(codes);

        mockMvc.perform(get("/api/common/codes/{groupCodeId}", groupCodeId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response[0].codeName").value("그림책"));
    }

    @Test
    @WithMockUser("user")
    @DisplayName("공통 코드 조회 API 실패 테스트")
    void testGetCodesFail() throws Exception {
        Long invalidGroupCodeId = 999L;

        given(commonCodeService.getCodesByGroupCode(invalidGroupCodeId)).willThrow(
                new IllegalArgumentException("잘못된 그룹코드 ID"));

        mockMvc.perform(get("/api/common/codes/{groupCodeId}", invalidGroupCodeId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}