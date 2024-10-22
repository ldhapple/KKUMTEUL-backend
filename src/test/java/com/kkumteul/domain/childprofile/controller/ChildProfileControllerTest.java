package com.kkumteul.domain.childprofile.controller;

import com.kkumteul.domain.book.dto.BookLikeDto;
import com.kkumteul.domain.childprofile.dto.ChildProfileResponseDto;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.domain.history.dto.ChildPersonalityHistoryDto;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.mbti.entity.MBTIName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChildProfileController.class)
class ChildProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChildProfileService childProfileService;

    @Test
    @DisplayName("자녀 정보 조회 테스트 - 조회 성공")
    void getChildProfile_success() throws Exception {
        Long childProfileId = 1L;
        String childName = "childName";

        List<BookLikeDto> bookLikeList = List.of(
                new BookLikeDto(1L, "title1", new byte[0]),
                new BookLikeDto(2L, "title2", new byte[0])
        );

        List<ChildPersonalityHistoryDto> childPersonalityHistoryList = List.of(
                new ChildPersonalityHistoryDto(MBTIName.INFJ, "멋져요", new byte[0], LocalDateTime.now(), HistoryCreatedType.DIAGNOSIS),
                new ChildPersonalityHistoryDto(MBTIName.INFJ, "착해요", new byte[0], LocalDateTime.now(), HistoryCreatedType.DIAGNOSIS)
        );

        ChildProfileResponseDto childProfileResponseDto = new ChildProfileResponseDto(
                childName,
                bookLikeList,
                childPersonalityHistoryList
        );

        given(childProfileService.getChildProfileDetail(childProfileId)).willReturn(childProfileResponseDto);

        mockMvc.perform(get("/api/childProfiles/{childProfileId}", childProfileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.childName").value("childName"));
    }


}