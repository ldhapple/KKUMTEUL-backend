package com.kkumteul.domain.childprofile.controller;

import com.kkumteul.domain.childprofile.dto.ChildProfileResponseDto;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


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
}
