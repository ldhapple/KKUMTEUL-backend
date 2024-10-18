package com.kkumteul.domain.childprofile.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kkumteul.domain.childprofile.dto.ChildProfileDto;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.exception.ChildProfileNotFoundException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ChildProfileController.class)
class ChildProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChildProfileService childProfileService;

    @Test
    @DisplayName("자녀 프로필 조회 API 성공 테스트")
    void testGetChildProfiles() throws Exception {
        Long userId = 1L;

        List<ChildProfileDto> findChildProfiles = List.of(
                new ChildProfileDto(1L, "lee")
        );

        given(childProfileService.getChildProfile(userId)).willReturn(findChildProfiles);

        mockMvc.perform(get("/api/child-profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response[0].childName").value("lee"));
    }

//    @Test
//    @DisplayName("자녀 프로필 조회 실패 테스트 - 유저 아이디에 등록된 프로필이 없는 경우")
//    void testGetChildProfileNotFound() throws Exception {
//        Long invalidUserId = 999L;
//
//        given(childProfileService.getChildProfile(invalidUserId))
//                .willThrow(new ChildProfileNotFoundException(invalidUserId));
//
//        mockMvc.perform(get("/api/child-profile"))
//                .andExpect(status().isNotFound());
//    } Security 구현 후 재작성
}