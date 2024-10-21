package com.kkumteul.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkumteul.domain.childprofile.dto.ChildProfileDto;
import com.kkumteul.domain.childprofile.entity.Gender;
import com.kkumteul.domain.user.dto.UserResponseDto;
import com.kkumteul.domain.user.dto.UserUpdateRequestDto;
import com.kkumteul.domain.user.service.UserService;
import com.kkumteul.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;


import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("유저 정보 조회 테스트 - 조회 성공")
    void getUser_success() throws Exception {
        Long userId = 1L;
        ChildProfileDto childProfile = new ChildProfileDto(1L, "childName", Gender.MALE, new Date(), "childImage".getBytes());
        List<ChildProfileDto> childProfiles = List.of(childProfile);

        UserResponseDto userDto = new UserResponseDto(userId, "name", "image".getBytes(), "nickname", "01012345678", new Date(), childProfiles);

        given(userService.getUser(userId)).willReturn(userDto);

        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.username").value("name"))
                .andExpect(jsonPath("$.response.nickName").value("nickname"))
                .andExpect(jsonPath("$.response.phoneNumber").value("01012345678"))
                .andExpect(jsonPath("$.response.childProfileList[0].childName").value("childName"));
    }

    @Test
    @DisplayName("유저 정보 조회 테스트 - UserNotFound 예외 발생")
    void getUser_not_found_exception() throws Exception {
        Long userId = 999L;
        given(userService.getUser(userId)).willThrow(new UserNotFoundException("user not found: " + userId));
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user not found: " + userId))
                .andExpect(jsonPath("$.statusCode").value(404));

    }

    @Test
    @DisplayName("유저 정보 수정 테스트 - 수정 성공")
    public void updateUser_success() throws Exception {
        Long userId = 1L;
        byte[] newImage = "image".getBytes();
        String newNickname = "newNickname";
        String newPassword = "newPassword";
        String newPhoneNumber = "01056789009";

        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(newImage, newNickname, newPassword, newPhoneNumber);

        willDoNothing().given(userService).updateUser(userId, userUpdateRequestDto);

        mockMvc.perform(put("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(userUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("user update successfully"));
    }

    @Test
    @DisplayName("유저 정보 삭제 테스트 - 삭제 성공")
    void deleteUser_success() throws Exception {
        Long userId = 1L;

        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("user deleted successfully"));
    }

    @Test
    @DisplayName("유저 정보 삭제 테스트 - 삭제 실패")
    void deletedUser_fail_not_found_exception() throws Exception {
        Long userId = 999L;

        doThrow(new UserNotFoundException("user not found: " + userId))
                .when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user not found: " + userId));

    }

}
