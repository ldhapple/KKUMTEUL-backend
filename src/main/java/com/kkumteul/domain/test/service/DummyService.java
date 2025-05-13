package com.kkumteul.domain.test.service;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.CumulativeMBTIScore;
import com.kkumteul.domain.childprofile.entity.Gender;
import com.kkumteul.domain.childprofile.entity.GenreScore;
import com.kkumteul.domain.childprofile.entity.TopicScore;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.childprofile.repository.CumulativeMBTIScoreRepository;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.personality.repository.GenreRepository;
import com.kkumteul.domain.personality.repository.TopicRepository;
import com.kkumteul.domain.survey.dto.MBTISurveyAnswerDto;
import com.kkumteul.domain.survey.dto.SurveyResultRequestDto;
import com.kkumteul.domain.survey.service.pattern.SurveyFacade;
import com.kkumteul.domain.user.entity.Role;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DummyService {

    private final UserRepository userRepository;
    private final ChildProfileRepository childProfileRepository;
    private final CumulativeMBTIScoreRepository cumulativeMBTIScoreRepository;
    private final GenreRepository genreRepository;
    private final TopicRepository topicRepository;
    private final SurveyFacade surveyFacade;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createChildProfileAndSurvey(long i) {
        // 1. User 생성 및 저장
        User user = User.builder()
                .nickName("nickName" + i)
                .phoneNumber("01012345678")
                .birthDate(null)
                .name("name" + i)
                .password("password" + i)
                .role(Role.ROLE_USER)
                .profileImage(null)
                .build();
        userRepository.save(user);

        // 2. ChildProfile 생성 (양방향 연관관계를 위한 add 메서드 활용)
        ChildProfile childProfile = ChildProfile.builder()
                .profileImage(null)
                .name("profile" + i)
                .user(user)
                .gender(Gender.MALE)
                .build();

        // 3. 누적 MBTI Score 생성 및 연결
        CumulativeMBTIScore cumulativeMBTIScore = CumulativeMBTIScore.builder()
                .childProfile(childProfile)
                .eScore(0)
                .iScore(0)
                .sScore(0)
                .nScore(0)
                .tScore(0)
                .fScore(0)
                .pScore(0)
                .jScore(0)
                .build();
        cumulativeMBTIScoreRepository.save(cumulativeMBTIScore);

        // 4. GenreScore 생성 및 ChildProfile에 추가
        for (long j = 1; j <= 10; j++) {
            Genre genre = genreRepository.findById(j).orElseThrow();
            GenreScore genreScore = GenreScore.builder()
                    .genre(genre)
                    .score(0)
                    .build();
            childProfile.addGenreScore(genreScore);
        }

        // 5. TopicScore 생성 및 ChildProfile에 추가
        for (long j = 1; j <= 25; j++) {
            Topic topic = topicRepository.findById(j).orElseThrow();
            TopicScore topicScore = TopicScore.builder()
                    .score(0)
                    .topic(topic)
                    .build();
            childProfile.addTopicScore(topicScore);
        }

        // 6. ChildProfile 저장 (Cascade 옵션에 의해 GenreScore, TopicScore도 함께 저장)
        childProfileRepository.save(childProfile);

        // 7. 설문 데이터 생성 및 제출
        List<MBTISurveyAnswerDto> answers = new ArrayList<>();
        answers.add(new MBTISurveyAnswerDto("I", 5));
        answers.add(new MBTISurveyAnswerDto("S", 5));
        answers.add(new MBTISurveyAnswerDto("T", 5));
        answers.add(new MBTISurveyAnswerDto("J", 5));

        SurveyResultRequestDto requestDto = new SurveyResultRequestDto(
                childProfile.getId(), answers, List.of(1L, 2L, 3L), List.of(1L, 2L, 3L, 4L, 5L)
        );

        surveyFacade.submitSurvey(requestDto);
    }
}
