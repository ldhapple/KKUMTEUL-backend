package com.kkumteul.domain.user.controller;

import com.kkumteul.domain.user.dto.UserResponseDto;
import com.kkumteul.domain.user.dto.UserUpdateRequestDto;
import com.kkumteul.domain.user.service.UserService;
import com.kkumteul.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

import static com.kkumteul.util.ApiUtil.ApiSuccess;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    // 1. 유저 정보 조회
    @GetMapping("{userId}")
    public ApiSuccess<?> getUser(@PathVariable(name = "userId") Long userId) {
        //추후 JWT 토큰 구현되면, userId를 가져오는 방식 변경 (PathVariable 사용 X)

        UserResponseDto userResponseDto = userService.getUser(userId);
        return ApiUtil.success(userResponseDto);

    }

    // 2. 유저 정보 수정
    @PostMapping("{userId}")
    public ApiSuccess<?> updateUser(
            @PathVariable(name = "userId") Long userId,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "nickName") String nickName,
            @RequestPart(value = "password") String password,
            @RequestPart(value = "phoneNumber") String phoneNumber) throws IOException {
        // TODO: 추후 JWT 토큰 구현되면, userId를 가져오는 방식 변경 (PathVariable 사용 X)

        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(nickName, password, phoneNumber);

        userService.updateUser(userId, userUpdateRequestDto, profileImage);
        return ApiUtil.success("user update successfully");
    }

    // 3. 유저 정보 삭제
    @DeleteMapping("{userId}")
    public ApiSuccess<?> deleteUser(@PathVariable(name = "userId") Long userId) {
        // TODO: 추후 JWT 토큰 구현되면, userId를 가져오는 방식 변경 (PathVariable 사용 X)

        userService.deleteUser(userId);
        return ApiUtil.success("user deleted successfully");
    }

}
