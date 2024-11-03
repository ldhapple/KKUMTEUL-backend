package com.kkumteul.domain.user.controller;

import com.kkumteul.auth.dto.CustomUserDetails;
import com.kkumteul.domain.user.dto.RegisterDto;
import com.kkumteul.domain.user.dto.UserResponseDto;
import com.kkumteul.domain.user.dto.UserUpdateRequestDto;
import com.kkumteul.domain.user.service.UserService;
import com.kkumteul.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @GetMapping
    public ApiSuccess<?> getUser(@AuthenticationPrincipal CustomUserDetails user) {
        //추후 JWT 토큰 구현되면, userId를 가져오는 방식 변경 (PathVariable 사용 X)
        Long userId = user.getId();

        UserResponseDto userResponseDto = userService.getUser(userId);
        return ApiUtil.success(userResponseDto);

    }

    // 2. 유저 정보 수정
    @PatchMapping
    public ApiSuccess<?> updateUser(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "nickName", required = false) String nickName,
            @RequestPart(value = "password", required = false) String password,
            @RequestPart(value = "phoneNumber", required = false) String phoneNumber) throws IOException {
        // TODO: 추후 JWT 토큰 구현되면, userId를 가져오는 방식 변경 (PathVariable 사용 X)

        Long userId = user.getId();
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(nickName, password, phoneNumber);

        userService.updateUser(userId, userUpdateRequestDto, profileImage);
        return ApiUtil.success("user update successfully");
    }

    // 3. 유저 정보 삭제
    @DeleteMapping
    public ApiSuccess<?> deleteUser(@AuthenticationPrincipal CustomUserDetails user) {
        // TODO: 추후 JWT 토큰 구현되면, userId를 가져오는 방식 변경 (PathVariable 사용 X)
        Long userId = user.getId();

        userService.deleteUser(userId);
        return ApiUtil.success("user deleted successfully");
    }

    // 자녀 검증
    @GetMapping("/childProfiles/{childProfileId}")
    public ApiSuccess<?> validateChildProfile(@AuthenticationPrincipal CustomUserDetails user, @PathVariable(name = "childProfileId") Long childProfileId) {
        // TODO: 추후 JWT 토큰 구현되면, userId를 가져오는 방식 변경 (PathVariable 사용 X)
        Long userId = user.getId();
        userService.hasChildProfile(userId, childProfileId);
        return ApiUtil.success("자녀 프로필 정보가 성공적으로 검증되었습니다.");
    }

    @PostMapping("/register")
    public ApiSuccess<?> register(@RequestBody RegisterDto registerDto) {
        userService.register(registerDto);

        return ApiUtil.success("회원가입이 완료되었습니다.");
    }

    @GetMapping("/duplicate/username/{username}")
    public ApiSuccess<?> checkDuplicateUsername(@PathVariable String username) {
        boolean isDuplicate = userService.duplicateUsername(username);

        return ApiUtil.success(isDuplicate);
    }

    @GetMapping("/duplicate/nickname/{nickname}")
    public ApiSuccess<?> checkDuplicateNickname(@PathVariable String nickname) {
        boolean isDuplicate = userService.duplicateNickname(nickname);
        return ApiUtil.success(isDuplicate);
    }
}
