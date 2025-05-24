package com.kkumteul.config.job.processor;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.dto.ScoreUpdateEventDto;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CachingScoreUpdateProcessor implements ItemProcessor<String, ScoreUpdateEventDto> {

    private final ChildProfileService childProfileService;
    private final BookService bookService;

    private Map<Long, ChildProfile> childProfileCache = new ConcurrentHashMap<>();
    private Map<Long, Book> bookCache = new ConcurrentHashMap<>();

    @Override
    public ScoreUpdateEventDto process(String item) throws Exception {
        String[] data = item.split(":");
        Long childProfileId = Long.parseLong(data[0]);
        Long bookId = Long.parseLong(data[1]);
        String action = data[2];

        double changedScore = action.equals("LIKE") ? 2.0 : -2.0;

        if (!childProfileCache.containsKey(childProfileId)) {
            ChildProfile childProfile = childProfileService.getChildProfileWithMBTIScore(childProfileId);
            childProfileCache.put(childProfileId, childProfile);
        }

        Book book = bookCache.computeIfAbsent(bookId, id -> bookService.getBook(id));

        Long genreId = book.getGenre().getId();

        Map<Long, Double> topicDelta = new HashMap<>();
        book.getBookTopics().forEach(bookTopic -> {
            Long topicId = bookTopic.getTopic().getId();
            topicDelta.put(topicId, changedScore);
        });

        ScoreUpdateEventDto event = new ScoreUpdateEventDto();
        event.setChildProfileId(childProfileId);
        event.getGenreDeltas().put(genreId, changedScore);
        event.setTopicDeltas(topicDelta);
        event.setCumulativeDelta(changedScore);
        event.setOriginalEvent(item);

        return event;
    }

    public void clearCache() {
        childProfileCache.clear();
        bookCache.clear();
        log.debug("CachingScoreUpdateProcessor: 캐시 초기화");
    }
}
