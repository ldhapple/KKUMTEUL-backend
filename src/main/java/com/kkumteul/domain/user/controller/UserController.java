package com.kkumteul.domain.user.controller;

import com.kkumteul.domain.user.dto.UserResponseDto;
import com.kkumteul.domain.user.dto.UserUpdateRequestDto;
import com.kkumteul.domain.user.service.UserService;
import com.kkumteul.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


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
    @PutMapping("{userId}")
    public ApiSuccess<?> updateUser(@PathVariable(name = "userId") Long userId, @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        // TODO: 추후 JWT 토큰 구현되면, userId를 가져오는 방식 변경 (PathVariable 사용 X)

        userService.updateUser(userId, userUpdateRequestDto);
        return ApiUtil.success("user update successfully");
    }

}
