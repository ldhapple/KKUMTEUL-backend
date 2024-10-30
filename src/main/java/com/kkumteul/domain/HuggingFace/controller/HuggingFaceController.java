package com.kkumteul.domain.HuggingFace.controller;

import com.kkumteul.domain.HuggingFace.dto.HuggingRequestDto;
import com.kkumteul.domain.HuggingFace.service.HuggingFaceService;
import com.kkumteul.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.kkumteul.util.ApiUtil.ApiSuccess;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hugging")
public class HuggingFaceController {

    private final HuggingFaceService huggingFaceService;

    @PostMapping("/mbti")
    public ApiSuccess<?> newBookLinkMbti(@RequestBody HuggingRequestDto request) {
        String analyzedBook = huggingFaceService.newBookLinkMbti(request);
        return ApiUtil.success(analyzedBook);
    }

    @PostMapping("/update-mbti")
    public ApiSuccess<?> updateBooksLinkMbti() {
        huggingFaceService.updateBooksLinkMbti();
        return ApiUtil.success("도서에 MBTI 성향이 연결 되었습니다.");
    }

}
