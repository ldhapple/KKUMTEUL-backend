package com.kkumteul.domain.recommendation.service;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.repository.BookRepository;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.ChildPersonalityHistoryGenre;
import com.kkumteul.domain.history.entity.ChildPersonalityHistoryTopic;
import com.kkumteul.domain.history.repository.ChildPersonalityHistoryGenreRepository;
import com.kkumteul.domain.history.repository.ChildPersonalityHistoryRepository;
import com.kkumteul.domain.history.repository.ChildPersonalityHistoryTopicRepository;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.entity.MBTIScore;
import com.kkumteul.domain.mbti.repository.MBTIRepository;
import com.kkumteul.domain.mbti.repository.MBTIScoreRepository;
import com.kkumteul.domain.recommendation.filter.CollaborativeFilter;
import com.kkumteul.domain.recommendation.filter.ContentBasedFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ChildProfileRepository childProfileRepository;
    private final ChildPersonalityHistoryRepository childPersonalityHistoryRepository;
    private final ChildPersonalityHistoryGenreRepository childPersonalityHistoryGenreRepository;
    private final ChildPersonalityHistoryTopicRepository childPersonalityHistoryTopicRepository;

    private final MBTIScoreRepository mbtiScoreRepository;
    private final MBTIRepository mbtiRepository;

    private final BookRepository bookRepository;
    private final ContentBasedFilter contentBasedFilter = new ContentBasedFilter();
    private final CollaborativeFilter collaborativeFilter = new CollaborativeFilter();


    // 추천 도서 목록 생성
    public List<Book> getRecommendations(Long userId) {
        ChildProfile childProfile = childProfileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 자녀의 최신 히스토리 기록 조회
        ChildPersonalityHistory childPersonalityHistory = childPersonalityHistoryRepository.findByProfileId(userId);

        // 자녀의 히스토리 기록이 없으면?
        if(childPersonalityHistory == null){
            // 다른 추천 로직 (기본) or 진단 하고 오세요 안내
            // 추후 구현

        }

        // 자녀의 선호 주제어 리스트
        List<ChildPersonalityHistoryTopic> childTopicList = childPersonalityHistoryTopicRepository.findByHistoryId(childPersonalityHistory.getId());

        // 자녀의 선호 장르 리스트
        List<ChildPersonalityHistoryGenre> childGenreList = childPersonalityHistoryGenreRepository.findByHistoryId(childPersonalityHistory.getId());

        // 자녀의 MBTI
        MBTIScore mbtiScore = mbtiScoreRepository.findById(childPersonalityHistory.getMbtiScore().getId())
                .orElseThrow(() -> new IllegalArgumentException("MBTI 점수가 존재하지 않습니다."));

        MBTI mbti = mbtiRepository.findById(mbtiScore.getId())
                .orElseThrow(() -> new IllegalArgumentException("MBTI가 존재하지 않습니다."));

        // 모든 도서 조회
        List<Book> allBooks = bookRepository.findAll();

        // 추천된 책 담을 리스트
        List<Book> recommendedBooks = new ArrayList<>();

        // 1. 콘텐츠 기반 필터링 (자녀의 선호 장르, 선호 주제어, MBTI와 일치하는 도서 필터링)
        List<Book> contentBasedBooks = contentBasedFilter.filterBooksByUserPreferences(childProfile, allBooks, childTopicList, childGenreList, mbti);

        // 2. 협업 필터링 - 유사한 사용자 찾기

        // 3. Fallback: 유사한 사용자가 없을 때 자신이 좋아한 도서와 유사한 도서 추천

        return recommendedBooks;
    }
}
