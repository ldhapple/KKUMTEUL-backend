package com.kkumteul.domain.history.controller;

import com.kkumteul.domain.history.dto.ChildPersonalityHistoryDetailDto;
import com.kkumteul.domain.history.service.ChildPersonalityHistoryService;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class ChildPersonalityHistoryController {

    private final ChildPersonalityHistoryService historyService;

    @GetMapping("/{historyId}")
    public ApiSuccess<?> getHistoryDetail(@PathVariable(name = "historyId") Long historyId, @RequestParam(name = "profileId") Long profileId) {
        ChildPersonalityHistoryDetailDto historyDetail = historyService.getHistoryDetail(profileId, historyId);

        return ApiUtil.success(historyDetail);
    }
  
    @DeleteMapping("/{historyId}")
    public ApiSuccess<?> deleteHistory(@PathVariable(name = "historyId") Long historyId) {
        historyService.deleteHistory(historyId);

        return ApiUtil.success("히스토리가 성공적으로 삭제되었습니다.");
    }
}
