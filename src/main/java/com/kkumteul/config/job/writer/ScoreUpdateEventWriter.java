package com.kkumteul.config.job.writer;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.domain.childprofile.service.ChildProfileUpdateService;
import com.kkumteul.domain.childprofile.service.PersonalityScoreService;
import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.history.service.ChildPersonalityHistoryService;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.service.MBTIService;
import com.kkumteul.dto.ScoreUpdateEventDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
public class ScoreUpdateEventWriter implements ItemWriter<ScoreUpdateEventDto>, StepExecutionListener {

    private final ChildProfileUpdateService childProfileUpdateService;

    private StepExecution stepExecution;

    @Override
    public void write(Chunk<? extends ScoreUpdateEventDto> chunk) throws Exception {
        Map<Long, ScoreUpdateEventDto> aggregatedMap = new HashMap<>();

        for (ScoreUpdateEventDto event : chunk) {
            ScoreUpdateEventDto agg = aggregatedMap.get(event.getChildProfileId());

            if (agg == null) {
                agg = new ScoreUpdateEventDto();
                agg.setChildProfileId(event.getChildProfileId());
                agg.setCumulativeDelta(0);
                agg.setGenreDeltas(new HashMap<>());
                agg.setTopicDeltas(new HashMap<>());
                aggregatedMap.put(event.getChildProfileId(), agg);
            }

            agg.setCumulativeDelta(agg.getCumulativeDelta() + event.getCumulativeDelta());

            final Map<Long, Double> currentGenreDeltas = agg.getGenreDeltas();
            event.getGenreDeltas().forEach((genreId, delta) ->
                    currentGenreDeltas.merge(genreId, delta, Double::sum)
            );

            final Map<Long, Double> currentTopicDeltas = agg.getTopicDeltas();
            event.getTopicDeltas().forEach((topicId, delta) ->
                    currentTopicDeltas.merge(topicId, delta, Double::sum)
            );
        }

        aggregatedMap.forEach(childProfileUpdateService::updateScoresAndHistory);

        List<String> originalEvents = chunk.getItems().stream()
                .map(ScoreUpdateEventDto::getOriginalEvent)
                .toList();

        stepExecution.getExecutionContext().put("batchItems", originalEvents);
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }

    //    private void createAndUpdateHistory(ChildProfile childProfile) {
//        MBTIScore currentMBTIScore = MBTIScore.fromCumulativeScore(childProfile.getCumulativeMBTIScore());
//
//         MBTI mbti = mbtiService.getMBTI(mbtiService.checkMBTIType(currentMBTIScore));
//         currentMBTIScore.setMbti(mbti);
//
//         ChildPersonalityHistory history = historyService.createHistory(childProfile.getId(), currentMBTIScore, HistoryCreatedType.FEEDBACK);
//
//         historyService.updatePreferredGenresByScore(history, childProfile.getGenreScores());
//         historyService.updatePreferredTopicsByScore(history, childProfile.getTopicScores());
//         log.info("Create History - ChildProfile ID: {}", childProfile.getId());
//    }
}
