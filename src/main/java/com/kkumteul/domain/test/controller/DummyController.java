package com.kkumteul.domain.test.controller;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.CumulativeMBTIScore;
import com.kkumteul.domain.childprofile.entity.Gender;
import com.kkumteul.domain.childprofile.entity.GenreScore;
import com.kkumteul.domain.childprofile.entity.TopicScore;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.childprofile.repository.CumulativeMBTIScoreRepository;
import com.kkumteul.domain.childprofile.repository.GenreScoreRepository;
import com.kkumteul.domain.childprofile.repository.TopicScoreRepository;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.repository.GenreRepository;
import com.kkumteul.domain.personality.repository.TopicRepository;
import com.kkumteul.domain.survey.dto.MBTISurveyAnswerDto;
import com.kkumteul.domain.survey.dto.SurveyResultRequestDto;
import com.kkumteul.domain.survey.service.pattern.SurveyFacade;
import com.kkumteul.domain.test.service.DummyService;
import com.kkumteul.domain.user.entity.Role;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.repository.UserRepository;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/dummy")
public class DummyController {

    private final DummyService dummyService;

    @PostMapping("/childProfiles")
    public ApiSuccess<?> childProfiles() {

        for (long i = 1; i <= 1000; i++) {
            try {
                dummyService.createChildProfileAndSurvey(i);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        return ApiUtil.success("더미데이터 삽입");
    }
}
