package com.kkumteul.domain.mbti.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.mbti.dto.MBTIPercentageDto;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.entity.MBTIName;
import com.kkumteul.domain.mbti.repository.MBTIRepository;
import com.kkumteul.domain.survey.dto.MBTISurveyAnswerDto;
import com.kkumteul.exception.InvalidMBTINameException;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MBTIServiceTest {

    @Mock
    private MBTIRepository mbtiRepository;

    @InjectMocks
    private MBTIService mbtiService;

    @Test
    @DisplayName("설문 결과를 통한 MBTI 점수 계산 테스트")
    void testCalculateMBTIScore() {
        List<MBTISurveyAnswerDto> answers = List.of(
                new MBTISurveyAnswerDto("I", 6),
                new MBTISurveyAnswerDto("I", 3),
                new MBTISurveyAnswerDto("E", 1),
                new MBTISurveyAnswerDto("E", 6),
                new MBTISurveyAnswerDto("S", 3),
                new MBTISurveyAnswerDto("F", 2),
                new MBTISurveyAnswerDto("P", 1),
                new MBTISurveyAnswerDto("J", 5)
        );

        MBTI mockMBTI = mock(MBTI.class);
        given(mbtiRepository.findByMbti(any())).willReturn(Optional.of(mockMBTI));

        MBTIScore mbtiScore = mbtiService.calculateMBTIScore(answers);

        assertThat(mbtiScore.getIScore()).isEqualTo(5);
        assertThat(mbtiScore.getEScore()).isEqualTo(3);
        assertThat(mbtiScore.getSScore()).isEqualTo(0);
        assertThat(mbtiScore.getFScore()).isEqualTo(0);
        assertThat(mbtiScore.getPScore()).isEqualTo(0);
        assertThat(mbtiScore.getJScore()).isEqualTo(4);
    }

    @Test
    @DisplayName("MBTI 타입 계산 테스트")
    void testCheckMBTIType() {
        String mbtiType = mbtiService.checkMBTIType(3, 1, 2, 4, 5, 3, 6, 2);

        assertThat(mbtiType).isEqualTo("INTJ");
    }

    @Test
    @DisplayName("MBTI Entity 조회 테스트 - 성공")
    void testGetMBTISuccess() {
        MBTI mbti = MBTI.builder()
                .mbti(MBTIName.ENFJ)
                .build();
        given(mbtiRepository.findByMbti(MBTIName.ENFJ)).willReturn(Optional.of(mbti));

        MBTI result = mbtiService.getMBTI("ENFJ");
        assertThat(result.getMbti()).isEqualTo(mbti.getMbti());
    }

    @Test
    @DisplayName("MBTI Entity 조회 테스트 - 실패")
    void testGetMBTIFail() {
        //만약 MBTI 엔티티에 존재하지 않은 MBTI일 경우.
        given(mbtiRepository.findByMbti(MBTIName.ENFJ)).willReturn(Optional.empty());

        assertThrows(InvalidMBTINameException.class, () -> mbtiService.getMBTI("ENFJ"));
    }

    @Test
    @DisplayName("MBTI 퍼센트 계산 테스트")
    void testCalculateMBTIPercentage() {
        MBTIScore mbtiScore = MBTIScore.builder()
                .iScore(4)
                .eScore(6)
                .sScore(3)
                .nScore(7)
                .tScore(5)
                .fScore(5)
                .jScore(6)
                .pScore(4)
                .build();

        MBTIPercentageDto result = mbtiService.calculatePercentages(mbtiScore);

        assertThat(result.getIPercent()).isEqualTo(40.0);
        assertThat(result.getEPercent()).isEqualTo(60.0);
    }
}