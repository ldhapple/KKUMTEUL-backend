package com.kkumteul.domain.user.controller;

import com.kkumteul.domain.user.dto.UserResponseDto;
import com.kkumteul.domain.user.dto.UserUpdateRequestDto;
import com.kkumteul.domain.user.service.UserService;
import com.kkumteul.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    @PatchMapping("{userId}")
    public ApiSuccess<?> updateUser(
            @PathVariable(name = "userId") Long userId,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "nickName", required = false) String nickName,
            @RequestPart(value = "password", required = false) String password,
            @RequestPart(value = "phoneNumber", required = false) String phoneNumber) throws IOException {
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

    // 자녀 검증
    @GetMapping("/{userId}/childProfiles/{childProfileId}")
    public ApiSuccess<?> validateChildProfile(@PathVariable(name = "userId") Long userId, @PathVariable(name = "childProfileId") Long childProfileId) {
        // TODO: 추후 JWT 토큰 구현되면, userId를 가져오는 방식 변경 (PathVariable 사용 X)
        userService.hasChildProfile(userId, childProfileId);
        return ApiUtil.success("자녀 프로필 정보가 성공적으로 검증되었습니다.");
    }

    @GetMapping("/protected-resource")
    public ResponseEntity<String> getProtectedResource(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok("Hello, " + username + "! This is a protected resource.");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> getAdminDashboard(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok("Hello Admin, " + username + "! This is the admin dashboard.");
    }

    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.duplicateUsername(username);
        return ResponseEntity.ok(exists); // 존재하면 true, 존재하지 않으면 false 반환
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickName) {
        boolean exists = userService.duplicateNickname(nickName);
        return ResponseEntity.ok(exists); // 존재하면 true, 존재하지 않으면 false 반환
    }
}
