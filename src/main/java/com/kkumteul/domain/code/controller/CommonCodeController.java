package com.kkumteul.domain.code.controller;

import com.kkumteul.domain.code.dto.CodeDto;
import com.kkumteul.domain.code.service.CommonCodeService;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/common")
public class CommonCodeController {

    private final CommonCodeService commonCodeService;

    @GetMapping("/codes/{groupCodeId}")
    public ApiSuccess<?> getCodesByGroupCode(@PathVariable(name = "groupCodeId") Long groupCodeId) {
        List<CodeDto> codes = commonCodeService.getCodesByGroupCode(groupCodeId);

        return ApiUtil.success(codes);
    }
}
