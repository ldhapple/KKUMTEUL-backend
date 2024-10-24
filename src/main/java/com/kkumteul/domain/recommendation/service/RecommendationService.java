package com.kkumteul.domain.recommendation.service;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookTopic;
import com.kkumteul.domain.book.repository.BookLikeRepository;
import com.kkumteul.domain.book.repository.BookRepository;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.FavoriteGenre;
import com.kkumteul.domain.history.entity.FavoriteTopic;
import com.kkumteul.domain.recommendation.dto.RecommendBookDto;
import com.kkumteul.domain.recommendation.repository.RecommendationRepository;
import com.kkumteul.exception.RecommendationBookNotFoundException;

import com.kkumteul.domain.history.repository.ChildPersonalityHistoryRepository;
import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.recommendation.dto.*;
import com.kkumteul.domain.recommendation.filter.CollaborativeFilter;
import com.kkumteul.domain.recommendation.filter.ContentBasedFilter;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import com.kkumteul.domain.recommendation.repository.RecommendationRepository;
import com.kkumteul.exception.RecommendationBookNotFoundException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final ChildProfileRepository childProfileRepository;
    private final ChildPersonalityHistoryRepository historyRepository;
    private final BookRepository bookRepository;
    private final BookLikeRepository likeRepository;
    private final ContentBasedFilter contentBasedFilter;
    private final CollaborativeFilter collaborativeFilter;
    private final RecommendationRepository recommendationRepository;

    public List<RecommendBookDto> getRecommendedBooks(Long childProfileId) {
        log.info("getRecommendedBooks - Input childProfileId: {}", childProfileId);
        List<Book> recommendBooks = recommendationRepository.findBookByChildProfileId(childProfileId)
                .orElseThrow(() -> new RecommendationBookNotFoundException(childProfileId));

        log.info("found recommendedBooks: {}", recommendBooks.size());
        return recommendBooks.stream()
                .map(RecommendBookDto::fromEntity)
                .toList();
    }

    // 추천 로직
    public List<BookDataDto> getRecommendations(Long userId) {
        // 1. 자녀 프로필 정보 조회
        ChildProfile childProfile = childProfileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 자녀 정보 가져오기
        ChildDataDto childDataDto = getChildInfo(childProfile.getId())
                .orElseThrow(() -> new IllegalArgumentException("정보 조회에 문제가 발생했습니다."));

        // 3. 도서 정보 가져오기
        List<Book> books = bookRepository.findAllBooksWithTopicsAndGenre();
        List<BookDataDto> allBooks = getAllBookInfo(books);

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
                .limit(50) // 50개 뽑아내기
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 8. Fallback: 추천 목록이 5개 미만일 경우 기본 추천 목록으로 채우기(책 추천 연령대로 10살 차이 안으로 추천(ex. 10세부터면 10~15살))
        if (recommendedBooks.size() < 5) {
            List<BookDataDto> defaultRecommendations = getDefaultRecommendations(childDataDto); // 5개
            recommendedBooks.addAll(defaultRecommendations.subList(0, Math.min(5 - recommendedBooks.size(), defaultRecommendations.size())));
        }

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
        for (FavoriteTopic ht : history.getFavoriteTopics()) {
            topics.add(new TopicDto(ht.getTopic().getId(), ht.getTopic().getName()));
        }

        List<GenreDto> genres = new ArrayList<>();
        for (FavoriteGenre hg : history.getFavoriteGenres()) {
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
    private List<BookDataDto> getAllBookInfo(List<Book> books) {
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


    // 기본 추천 목록 - 사실 가중치 점수 때문에 나이, 성별로 추천 되는 책이 있어서... 여기까지 갈 일은 없겠지만 그냥 책 추천 연령대로 10살 차이 안으로 추천(ex. 10세부터면 10~15살)
    private List<BookDataDto> getDefaultRecommendations(ChildDataDto childDataDto){
        int age = getAge(childDataDto.getBirthDate());

        Pageable pageable = PageRequest.of(0, 5);
        List<Book> bookList = bookRepository.findBookListByAgeGroup(age, pageable);

        return getAllBookInfo(bookList);
    }

    // 생년월일을 나이로 변환하는 메서드
    private int getAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}

