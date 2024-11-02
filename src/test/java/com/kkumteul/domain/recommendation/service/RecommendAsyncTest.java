package com.kkumteul.domain.recommendation.service;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.recommendation.service.AsyncService;
import com.kkumteul.domain.recommendation.service.RecommendationService;
import com.kkumteul.domain.recommendation.service.TransactionalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendAsyncTest {

    @Mock
    private ChildProfileRepository childProfileRepository;

    @Mock
    private AsyncService asyncService;

    @Mock
    private TransactionalService transactionalService;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    @DisplayName("사용자 활동 기록 비동기")
    public void testUpdateLastActivityAsync() throws InterruptedException {
        // Mock 데이터 설정
        ChildProfile mockProfile = ChildProfile.builder().name("김은주").build();
        given(childProfileRepository.findById(1L)).willReturn(Optional.of(mockProfile));

        // 불필요한 Mock 설정이 없도록 doAnswer로 처리
        doAnswer(invocation -> {
            Long profileId = invocation.getArgument(0);
            ChildProfile profile = childProfileRepository.findById(profileId)
                    .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
            childProfileRepository.save(profile);
            return null;
        }).when(asyncService).updateLastActivity(any(Long.class));

        // RecommendationService의 비동기 메서드 호출
        recommendationService.updateLastActivity(1L);

        // 비동기 작업 완료 대기
        Thread.sleep(2000);

        // 검증: save 메서드가 1회 호출되었는지 확인
        verify(childProfileRepository, times(1)).save(any(ChildProfile.class));
    }

}
