package com.kkumteul.domain.recommendation.service;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookTopic;
import com.kkumteul.domain.book.repository.BookLikeRepository;
import com.kkumteul.domain.book.repository.BookRepository;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.ChildPersonalityHistoryGenre;
import com.kkumteul.domain.history.entity.ChildPersonalityHistoryTopic;
import com.kkumteul.domain.history.repository.ChildPersonalityHistoryRepository;
import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.recommendation.dto.BookDataDto;
import com.kkumteul.domain.recommendation.dto.ChildDataDto;
import com.kkumteul.domain.recommendation.dto.GenreDto;
import com.kkumteul.domain.recommendation.dto.TopicDto;
import com.kkumteul.domain.recommendation.filter.CollaborativeFilter;
import com.kkumteul.domain.recommendation.filter.ContentBasedFilter;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ChildProfileRepository childProfileRepository;
    private final ChildPersonalityHistoryRepository historyRepository;
    private final BookRepository bookRepository;
    private final BookLikeRepository likeRepository;
    private final ContentBasedFilter contentBasedFilter = new ContentBasedFilter();
    private final CollaborativeFilter collaborativeFilter = new CollaborativeFilter();

    // 추천 로직
    public List<BookDataDto> getRecommendations(Long userId) {
        // 1. 자녀 프로필 정보 조회
        ChildProfile childProfile = childProfileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 자녀 정보 가져오기
        ChildDataDto childDataDto = getChildInfo(childProfile.getId())
                .orElseThrow(() -> new IllegalArgumentException("정보 조회에 문제가 발생했습니다."));

        // 3. 도서 정보 가져오기
        List<BookDataDto> allBooks = getAllBookInfo();

        // 4. 콘텐츠 기반 필터링 수행 (가중치 2점 부여)
        Set<BookDataDto> contentBasedBooks = contentBasedFilter.filterBooksByUserPreferences(childDataDto, allBooks);
        Map<BookDataDto, Double> scoredBooks = new HashMap<>();
        contentBasedBooks.forEach(book -> scoredBooks.put(book, 2.0));

        // 5. 협업 필터링 수행
        List<ChildDataDto> childDataList = getChildrenInfo();
        List<ChildDataDto> similarProfiles = collaborativeFilter.findSimilarProfiles(childDataDto, childDataList); // 비어있음

        // 6. 협업 필터링 - 유사한 사용자의 좋아요 도서 가중치 점수 누적해서 가져오기
        Map<BookDataDto, Double> finalScores = setCollaborativeScores(similarProfiles, scoredBooks);

        // 7. 최종 추천 도서 필터링 (점수 상위 100개 뽑아내기)
        List<BookDataDto> recommendedBooks = finalScores.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())) // 점수 내림차순 정렬
                .limit(100) // 100개 뽑아내기
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 8. Fallback: 추천 목록이 5개 미만일 경우 기본 추천 목록으로 채우기
//        if (recommendedBooks.size() < 5) {
//            List<BookDataDto> defaultRecommendations = getDefaultRecommendations();
//            recommendedBooks.addAll(defaultRecommendations.subList(0, Math.min(5 - recommendedBooks.size(), defaultRecommendations.size())));
//        }

        return recommendedBooks;
    }

    // 자녀 정보 한꺼번에 가져오기
    private Optional<ChildDataDto> getChildInfo(Long profileId){
        // 자녀 추천 로직 관련 정보 한꺼번에 가져오기
        Pageable pageable = PageRequest.of(0, 1); // 상위 1개만 가져오기

        Page<ChildPersonalityHistory> historyList = historyRepository.findChildData(profileId, pageable);
        if(historyList.isEmpty()){
            return Optional.empty();
        }

        ChildPersonalityHistory history = historyList.getContent().get(0);

        List<TopicDto> topics = new ArrayList<>();
        for (ChildPersonalityHistoryTopic ht : history.getHistoryTopics()) {
            topics.add(new TopicDto(ht.getTopic().getId(), ht.getTopic().getName()));
        }

        List<GenreDto> genres = new ArrayList<>();
        for (ChildPersonalityHistoryGenre hg : history.getHistoryGenres()) {
            genres.add(new GenreDto(hg.getGenre().getId(), hg.getGenre().getName()));
        }

        ChildDataDto childDataDto = ChildDataDto.builder()
                .id(history.getChildProfile().getId())
                .gender(history.getChildProfile().getGender())
                .birthDate(history.getChildProfile().getBirthDate())
                .mbti(history.getMbtiScore().getMbti().getMbti())
                .topics(topics)
                .genres(genres)
                .build();

        return Optional.of(childDataDto);
    }

    // 도서 정보 한꺼번에 가져오기 위해 ( + 데이터 dto로 매핑 )
    private List<BookDataDto> getAllBookInfo() {

        // 1. 도서 목록과 연관된 장르 및 주제어 조회
        List<Book> books = bookRepository.findAllBooksWithTopicsAndGenre();
        List<BookDataDto> bookDataDtos = new ArrayList<>();

        // 2. 도서 목록 순회하며 BookDataDto 생성
        for (Book book : books) {

            // 주제어 리스트 생성
            List<TopicDto> topicDtos = new ArrayList<>();

            for (BookTopic bookTopic : book.getBookTopics()) {
                Topic topic = bookTopic.getTopic();
                TopicDto topicDto = new TopicDto(topic.getId(), topic.getName());
                topicDtos.add(topicDto);
            }

            // BookDataDto
            BookDataDto bookDataDto = BookDataDto.builder()
                    .bookId(book.getId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .genreDto(new GenreDto(book.getGenre().getId(), book.getGenre().getName()))
                    .topics(topicDtos)
                    .build();

            bookDataDtos.add(bookDataDto);
        }

        return bookDataDtos;
    }

    // 협업 필터링에 사용 될 모든 자녀 정보(성별, 생년월일, mbti)
    private List<ChildDataDto> getChildrenInfo() {
        List<ChildPersonalityHistory> histories = historyRepository.findAllChildrenData();

        List<ChildDataDto> childDataAll = new ArrayList<>();

        for (ChildPersonalityHistory history : histories) {
            ChildDataDto dto = ChildDataDto.builder()
                    .id(history.getChildProfile().getId())
                    .gender(history.getChildProfile().getGender())
                    .birthDate(history.getChildProfile().getBirthDate())
                    .mbti(history.getMbtiScore().getMbti().getMbti())
                    .build();
            childDataAll.add(dto);
        }

        return childDataAll;
    }


    // 협업 필터링 된 도서 좋아요 목록 조회
    private Set<BookDataDto> getBooksList(Set<Long> childIds, Pageable pageable){

        // 유사한 사용자들이 좋아요 누른 책 반환
        Page<Book> likedBooks = likeRepository.findBookLikeByUser(childIds, pageable);

        return likedBooks.stream()
                .map(book -> BookDataDto.builder()
                        .bookId(book.getId())
                        .title(book.getTitle())
                        .author(book.getAuthor())
                        .genreDto(
                                GenreDto.builder()
                                        .genreId(book.getGenre().getId())
                                        .genreName(book.getGenre().getName())
                                        .build()
                        )
                        .topics(
                                book.getBookTopics().stream()
                                        .map(bookTopic -> TopicDto.builder()
                                                .topicId(bookTopic.getTopic().getId())
                                                .topicName(bookTopic.getTopic().getName())
                                                .build()
                                        )
                                        .collect(Collectors.toList())
                        )
                        .build()
                )
                .collect(Collectors.toSet()); // 중복 제거
    }

    // 유사한 사용자가 좋아요 한 도서에 유사도 점수 추가
    private Map<BookDataDto, Double> setCollaborativeScores(
            List<ChildDataDto> similarProfiles, Map<BookDataDto, Double> initialScores) {

        Pageable pageable = PageRequest.of(0, 20);
        Map<BookDataDto, Double> updatedScores = new HashMap<>(initialScores);

        // 유사한 사용자들의 좋아요 목록을 조회하고 점수를 누적
        for (ChildDataDto profile : similarProfiles) {
            double similarityScore = profile.getScore(); // 유사도 점수 가져오기

            // 유사한 사용자가 좋아요 한 도서 조회
            Set<BookDataDto> likedBooks = getBooksList(Set.of(profile.getId()), pageable);

            // 각 도서에 점수를 누적
            for (BookDataDto book : likedBooks) {
                updatedScores.put(book,
                        updatedScores.getOrDefault(book, 0.0) + similarityScore);
            }
        }
        return updatedScores;
    }


    // 기본 추천 목록 - 사실 가중치 점수 때문에 나이, 성별로 추천 되는 책이 있어서... 여기까지 갈 일은 없겠지만 그냥 책 추천 연령대로 추천하는 로직 생각 중
//    private List<BookDataDto> getDefaultRecommendations(){
//        bookRepository.find
//    }

}

