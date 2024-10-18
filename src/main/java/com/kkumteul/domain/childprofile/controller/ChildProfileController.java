package com.kkumteul.domain.childprofile.controller;

import com.kkumteul.domain.childprofile.dto.ChildProfileDto;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/child-profile")
public class ChildProfileController {

    private final ChildProfileService childProfileService;

    @GetMapping
    public ApiSuccess<?> getChildProfiles() {
        // 인증 인가 구현 시 @AuthenticationPrincipal 사용해서 UserID 가져오기.
        Long userId = 1L;

        List<ChildProfileDto> childProfiles = childProfileService.getChildProfile(userId);

        return ApiUtil.success(childProfiles);
    }
}
