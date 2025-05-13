package com.kkumteul.config.job.processor;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookMBTI;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.domain.childprofile.service.PersonalityScoreService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CachingEventProcessor implements ItemProcessor<String, Long> {

    private final ChildProfileService childProfileService;
    private final BookService bookService;
    private final PersonalityScoreService personalityScoreService;

    private Map<Long, ChildProfile> childProfileCache = new ConcurrentHashMap<>();
    private Map<Long, Book> bookCache = new ConcurrentHashMap<>();

    @Override
    public Long process(String item) throws Exception {
        String[] data = item.split(":");
        Long childProfileId = Long.parseLong(data[0]);
        Long bookId = Long.parseLong(data[1]);
        String like = data[2];

        ChildProfile childProfile = childProfileCache.get(childProfileId);
        if (childProfile == null) {
            childProfile = childProfileService.getChildProfileWithMBTIScore(childProfileId);
            childProfileCache.put(childProfileId, childProfile);
        }

        Book book = bookCache.get(bookId);
        if (book == null) {
            book = bookService.getBook(bookId);
            bookCache.put(bookId, book);
        }

        List<BookMBTI> bookMBTIs = book.getBookMBTIS();
        double changedScore = like.equals("LIKE") ? 2.0 : -2.0;

        personalityScoreService.updateGenreAndTopicScores(childProfile, book, changedScore);
        personalityScoreService.updateCumulativeMBTIScore(childProfileId, bookMBTIs, changedScore);

        return childProfileId;
    }

    public void clearCache() {
        childProfileCache.clear();
        bookCache.clear();
        log.debug("ChildProfile, Book 캐시 초기화");
    }
}
