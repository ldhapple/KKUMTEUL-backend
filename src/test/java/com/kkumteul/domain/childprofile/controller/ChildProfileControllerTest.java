package com.kkumteul.domain.childprofile.controller;

import com.kkumteul.auth.dto.CustomUserDetails;
import com.kkumteul.domain.book.dto.BookLikeDto;
import com.kkumteul.domain.childprofile.dto.ChildProfileInsertRequestDto;
import com.kkumteul.domain.childprofile.dto.ChildProfileResponseDto;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.domain.history.dto.ChildPersonalityHistoryDto;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.mbti.entity.MBTIName;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.exception.UserNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
@WithMockUser("user")
class ChildProfileControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChildProfileService childProfileService;

    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .nickName("nickname")
                .build();

        customUserDetails = new CustomUserDetails(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null));

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

    }

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
                new ChildPersonalityHistoryDto(1L, MBTIName.INFJ, "멋져요", new byte[0], LocalDateTime.now(), HistoryCreatedType.DIAGNOSIS),
                new ChildPersonalityHistoryDto(2L, MBTIName.INFJ, "착해요", new byte[0], LocalDateTime.now(), HistoryCreatedType.DIAGNOSIS)
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
                new ChildProfileDto(1L, "lee", null)
        );

        given(childProfileService.getChildProfileList(userId)).willReturn(findChildProfiles);

        mockMvc.perform(get("/api/childProfiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response[0].childName").value("lee"));
    }


    @Test
    @WithMockUser("user")
    @DisplayName("자녀 등록 테스트 - 등록 성공")
    void insert_childProfile_success() throws Exception {
        Long userId = customUserDetails.getId();
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
                        .param("childGender", childGender)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("child profile inserted successfully"));
    }

    @Test
    @DisplayName("자녀 삭제 테스트 - 삭제 성공")
    void deleteChildProfile_success() throws Exception {
        Long childProfileId = 1L;

        willDoNothing().given(childProfileService).deleteChildProfile(childProfileId);

        mockMvc.perform(delete("/api/childProfiles/{childProfileId}", childProfileId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("child profile deleted successfully"));
    }

    @Test
    @DisplayName("자녀 삭제 테스트 - 삭제 실패")
    void deleteChildProfile_fail() throws Exception {
        Long childProfileId = 999L;

        doThrow(new IllegalArgumentException("childProfile not found:" + childProfileId))
                .when(childProfileService).deleteChildProfile(childProfileId);

        mockMvc.perform(delete("/api/childProfiles/{childProfileId}", childProfileId)
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("childProfile not found:" + childProfileId));
    }
}