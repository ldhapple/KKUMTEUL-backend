package com.kkumteul.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkumteul.auth.dto.CustomUserDetails;
import com.kkumteul.domain.childprofile.dto.ChildProfileDetailDto;
import com.kkumteul.domain.childprofile.entity.Gender;
import com.kkumteul.domain.user.dto.UserResponseDto;
import com.kkumteul.domain.user.dto.UserUpdateRequestDto;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.service.UserService;
import com.kkumteul.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;


import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(UserController.class)
@WithMockUser
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


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
    @WithMockUser("user")
    @DisplayName("유저 정보 조회 테스트 - 조회 성공")
    void getUser_success() throws Exception {

        ChildProfileDetailDto childProfile = new ChildProfileDetailDto(1L, "childName", Gender.MALE, new Date(), "childImage".getBytes(), null);
        List<ChildProfileDetailDto> childProfiles = List.of(childProfile);

        UserResponseDto userDto = new UserResponseDto("name", "image".getBytes(), null, "nickname", "01012345678", new Date(), childProfiles);

        given(userService.getUser(customUserDetails.getId())).willReturn(userDto);

        mockMvc.perform(get("/api/users")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.nickName").value("nickname"))
                .andExpect(jsonPath("$.response.childProfileList[0].childName").value("childName"));
    }

    @Test
    @WithMockUser("user")
    @DisplayName("유저 정보 조회 테스트 - UserNotFound 예외 발생")
    void getUser_not_found_exception() throws Exception {

        Long userId = customUserDetails.getId();

        given(userService.getUser(userId)).willThrow(new UserNotFoundException("user not found: " + userId));
        mockMvc.perform(get("/api/users")
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user not found: " + userId))
                .andExpect(jsonPath("$.statusCode").value(404));

    }

    @Test
    @DisplayName("유저 정보 수정 테스트 - 수정 성공")
    public void updateUser_success() throws Exception {
        Long userId = customUserDetails.getId();
        byte[] newImage = "image".getBytes();
        String newNickname = "newNickname";
        String newPassword = "newPassword";
        String newPhoneNumber = "01056789009";

        MultipartFile multipartFile = new MockMultipartFile("childProfileImage", "profile.jpg", "image/jpeg", new byte[1024]);


        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(newNickname, newPassword, newPhoneNumber);

        willDoNothing().given(userService).updateUser(userId, userUpdateRequestDto, multipartFile);

        mockMvc.perform(patch("/api/users")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(userUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("user update successfully"));
    }

    @Test
    @DisplayName("유저 정보 삭제 테스트 - 삭제 성공")
    void deleteUser_success() throws Exception {
        Long userId = customUserDetails.getId();

        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("user deleted successfully"));
    }

    @Test
    @DisplayName("유저 정보 삭제 테스트 - 삭제 실패")
    void deletedUser_fail_not_found_exception() throws Exception {
        Long userId = customUserDetails.getId();

        doThrow(new UserNotFoundException("user not found: " + userId))
                .when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users")
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user not found: " + userId));

    }

}
