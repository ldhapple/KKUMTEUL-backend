package com.kkumteul.domain.survey.service.pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.history.service.ChildPersonalityHistoryService;
import com.kkumteul.domain.mbti.dto.MBTIPercentageDto;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.entity.MBTIName;
import com.kkumteul.domain.mbti.service.MBTIService;
import com.kkumteul.domain.survey.dto.MBTISurveyAnswerDto;
import com.kkumteul.domain.survey.dto.SurveyResultDto;
import com.kkumteul.domain.survey.dto.SurveyResultRequestDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SurveyFacadeTest {

    @Mock
    private MBTIService mbtiService;

    @Mock
    private ChildPersonalityHistoryService historyService;

    @Mock
    private ChildProfileService childProfileService;

    @InjectMocks
    private SurveyFacadeImpl surveyFacade;

    @Test
    @DisplayName("설문 제출 테스트")
    void testSubmitSurvey() {
        Long childProfileId = 1L;

        SurveyResultRequestDto requestDto = mock(SurveyResultRequestDto.class);
        ChildProfile childProfile = mock(ChildProfile.class);
        MBTIScore mbtiScore = mock(MBTIScore.class);

        given(childProfileService.getChildProfile(childProfileId)).willReturn(childProfile);
        given(mbtiService.calculateMBTIScore(requestDto.getAnswers())).willReturn(mbtiScore);

        surveyFacade.submitSurvey(requestDto, childProfileId);

        verify(childProfileService, times(1)).resetCumulativeMBTIScore(childProfileId);
        verify(childProfileService, times(1)).updateCumulativeMBTIScore(childProfileId, mbtiScore);
        verify(historyService, times(1)).deleteDiagnosisHistory(childProfileId);
        verify(historyService, times(1)).createHistory(childProfileId, mbtiScore, HistoryCreatedType.DIAGNOSIS);
        verify(childProfileService, times(1)).resetFavoriteScores(childProfileId);
    }

    @Test
    @DisplayName("설문 결과 조회 테스트")
    void testGetSurveyResult() {
        Long childProfileId = 1L;

        ChildProfile childProfile = mock(ChildProfile.class);
        ChildPersonalityHistory history = mock(ChildPersonalityHistory.class);
        MBTIScore mbtiScore = mock(MBTIScore.class);
        MBTI mbti = mock(MBTI.class);
        MBTIName mbtiName = MBTIName.INFJ;
        MBTIPercentageDto mbtiPercentageDto = mock(MBTIPercentageDto.class);

        given(childProfileService.getChildProfile(childProfileId)).willReturn(childProfile);
        given(historyService.getLatestHistory(childProfileId)).willReturn(history);
        given(history.getMbtiScore()).willReturn(mbtiScore);
        given(mbtiScore.getMbti()).willReturn(mbti);
        given(mbti.getMbti()).willReturn(mbtiName);
        given(mbtiService.calculatePercentages(history.getMbtiScore())).willReturn(mbtiPercentageDto);

        SurveyResultDto result = surveyFacade.getSurveyResult(childProfileId);

        assertThat(result).isNotNull();
        verify(historyService, times(1)).getLatestHistory(childProfileId);
        verify(mbtiService, times(1)).calculatePercentages(any());
    }

    @Test
    @DisplayName("재진단 테스트")
    void testReSurvey() {
        Long childProfileId = 1L;

        surveyFacade.reSurvey(childProfileId);

        verify(historyService, times(1)).deleteDiagnosisHistory(childProfileId);
    }
}