package com.kkumteul.domain.childprofile.controller;

import com.kkumteul.domain.book.dto.BookLikeDto;
import com.kkumteul.domain.childprofile.dto.ChildProfileInsertRequestDto;
import com.kkumteul.domain.childprofile.dto.ChildProfileResponseDto;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.domain.history.dto.ChildPersonalityHistoryDto;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.mbti.entity.MBTIName;
import com.kkumteul.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.kkumteul.domain.childprofile.dto.ChildProfileDto;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.exception.ChildProfileNotFoundException;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

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
  
    @DisplayName("자녀 프로필 조회 API 성공 테스트")
    void testGetChildProfiles() throws Exception {
        Long userId = 1L;

        List<ChildProfileDto> findChildProfiles = List.of(
                new ChildProfileDto(1L, "lee")
        );

        given(childProfileService.getChildProfileList(userId)).willReturn(findChildProfiles);

        mockMvc.perform(get("/api/childProfiles"))
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

    @Test
    @DisplayName("자녀 프로필 변경 테스트 - 성공")
    void testSwitchChildProfileSuccess() throws Exception {
        Long validProfileId = 1L;
        MockHttpSession session = new MockHttpSession();

        doNothing().when(childProfileService).validateChildProfile(validProfileId);
        //특정 메서드 호출 시 아무런 동작을 하지 않도록 함 -> doNothing()
        //validateChildProfile()이 아무런 동작을 하지 않아도 session에 정상적으로 담기는 지 확인할 수 있음.
        //오히려 예외를 던지면 안됨.

        mockMvc.perform(post("/api/childProfiles/switch")
                        .param("childProfileId", String.valueOf(validProfileId))
                        .session(session))
//                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("프로필이 성공적으로 변경되었습니다."));

        Long sessionProfileId = (Long) session.getAttribute("currentChildProfileId");
        assertThat(sessionProfileId).isEqualTo(validProfileId);
    }

    @Test
    @DisplayName("자녀 프로필 전환 실패 - 존재하지 않는 프로필")
    void testSwitchChildProfile_Fail() throws Exception {
        Long invalidProfileId = 999L;

        // 서비스에서 예외 발생 시 mock 처리
        doThrow(new IllegalArgumentException("childProfile not found - childProfileId : " + invalidProfileId))
                .when(childProfileService).validateChildProfile(invalidProfileId);

        mockMvc.perform(post("/api/childProfiles/switch")
                        .param("childProfileId", String.valueOf(invalidProfileId)))
//                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

//    @Test
//    @DisplayName("자녀 등록 테스트 - 등록 성공")
//    void insert_childProfile_success() throws Exception {
//        Long userId = 1L;
//        Long childProfileId = 1L;
//        String childName = "childName";
//        String childBirthDate = "19980905";
//        String childGender = "Male";
//        MockMultipartFile profileImage = new MockMultipartFile("childProfileImage", "profile.jpg", "image/jpeg", new byte[0]);
//
//        ChildProfileInsertRequestDto childProfileInsertRequestDto = new ChildProfileInsertRequestDto(childName, childGender, childBirthDate);
//
//        doNothing().when(childProfileService).insertChildProfile(userId, childProfileId, profileImage, childProfileInsertRequestDto);
//
//        mockMvc.perform(post("/api/childProfiles/{childProfileId}", childProfileId)
//                        .file(profileImage)
//                        .param("childName", childName)
//                        .param("childBirthDate", childBirthDate)
//                        .param("childGender", childGender))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.response").value("child profile inserted successfully"));
//
//        // Verify that the service method was called
//        verify(childProfileService).insertChildProfile(anyLong(), eq(childProfileId), any(MultipartFile.class), any(ChildProfileInsertRequestDto.class));
//    }

    @Test
    @DisplayName("자녀 등록 테스트 - 등록 성공")
    void insert_childProfile_success() throws Exception {
        Long userId = 1L;
        String childName = "childName";
        String childBirthDate = "19980905";
        String childGender = "Male";

        MockMultipartFile profileImage = new MockMultipartFile("childProfileImage", "profile.jpg", "image/jpeg", new byte[0]);
        ChildProfileInsertRequestDto childProfileInsertRequestDto = new ChildProfileInsertRequestDto(childName, childGender, childBirthDate);

        willDoNothing().given(childProfileService).insertChildProfile(userId, profileImage, childProfileInsertRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/childProfiles")
                        .file(profileImage)
                        .param("childName", childName)
                        .param("childBirthDate", childBirthDate)
                        .param("childGender", childGender))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("child profile inserted successfully"));
    }

    @Test
    @DisplayName("자녀 삭제 테스트 - 삭제 성공")
    void deleteChildProfile_success() throws Exception {
        Long childProfileId = 1L;

        willDoNothing().given(childProfileService).deleteChildProfile(childProfileId);

        mockMvc.perform(delete("/api/childProfiles/{childProfileId}", childProfileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("child profile deleted successfully"));
    }

    @Test
    @DisplayName("자녀 삭제 테스트 - 삭제 실패")
    void deleteChildProfile_fail() throws Exception {
        Long childProfileId = 999L;

        doThrow(new IllegalArgumentException("childProfile not found:" + childProfileId))
                .when(childProfileService).deleteChildProfile(childProfileId);

        mockMvc.perform(delete("/api/childProfiles/{childProfileId}", childProfileId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("childProfile not found:" + childProfileId));
    }
}