package com.kkumteul.domain.childprofile.controller;

import com.kkumteul.domain.childprofile.dto.ChildProfileInsertRequestDto;
import com.kkumteul.domain.childprofile.dto.ChildProfileResponseDto;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.kkumteul.domain.childprofile.dto.ChildProfileDto;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;
import jakarta.servlet.http.HttpSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/childProfiles")
public class ChildProfileController {
    private final ChildProfileService childProfileService;

    // 1. 자녀 프로필 상세 조회
    @GetMapping("/{childProfileId}")
    public ApiSuccess<?> getChildProfile(@PathVariable(name = "childProfileId") Long childProfileId) {
        ChildProfileResponseDto childProfile = childProfileService.getChildProfileDetail(childProfileId);
        return ApiUtil.success(childProfile);
    }

    @GetMapping
    public ApiSuccess<?> getChildProfiles() {
        // 인증 인가 구현 시 @AuthenticationPrincipal 사용해서 UserID 가져오기.
        Long userId = 1L;

        List<ChildProfileDto> childProfiles = childProfileService.getChildProfileList(userId);

        return ApiUtil.success(childProfiles);
    }

    //JWT 구현되면 Session방식에서 토큰 활용하는 방식으로 수정 예정
    @PostMapping("/switch")
    public ApiSuccess<?> switchChildProfile(@RequestParam(name = "childProfileId") Long childProfileId, HttpSession session) {
        childProfileService.validateChildProfile(childProfileId);

        session.setAttribute("currentChildProfileId", childProfileId);

        return ApiUtil.success("프로필이 성공적으로 변경되었습니다.");
    }

    // 자녀 등록
    @PostMapping("{childProfileId}")
    public ApiSuccess<?> insertChildProfile(
            @PathVariable(name = "childProfileId") Long childProfileId,
            @RequestPart(value = "childName", required = false) String childName,
            @RequestPart(value = "childBirthDate", required = false) String childBirthDate,
            @RequestPart(value = "childGender", required = false) String childGender,
            @RequestPart(value = "childProfileImage", required = false) MultipartFile childProfileImage
    ) throws IOException, ParseException {

        Long userId = 1L; // 더미

        ChildProfileInsertRequestDto childProfileInsertRequestDto = new ChildProfileInsertRequestDto(childName, childGender, childBirthDate);
        childProfileService.insertChildProfile(userId, childProfileId, childProfileImage, childProfileInsertRequestDto);

        return ApiUtil.success("child profile inserted successfully");

    }

    // 자녀 삭제
    @DeleteMapping("{childProfileId}")
    public ApiSuccess<?> deleteChildProfile(@PathVariable(name = "childProfileId") Long childProfileId) {

        childProfileService.deleteChildProfile(childProfileId);
        return ApiUtil.success("child profile deleted successfully");

    }

}
