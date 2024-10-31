package com.kkumteul.domain.recommendation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookMBTI;
import com.kkumteul.domain.book.entity.BookTopic;
import com.kkumteul.domain.book.repository.BookLikeRepository;
import com.kkumteul.domain.book.repository.BookRepository;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.FavoriteGenre;
import com.kkumteul.domain.history.entity.FavoriteTopic;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.entity.MBTIName;
import com.kkumteul.domain.recommendation.dto.RecommendBookDto;
import com.kkumteul.domain.recommendation.entity.Recommendation;
import com.kkumteul.domain.recommendation.repository.RecommendationRepository;
import com.kkumteul.exception.RecommendationBookNotFoundException;

import com.kkumteul.domain.history.repository.ChildPersonalityHistoryRepository;
import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.recommendation.dto.*;
import com.kkumteul.domain.recommendation.filter.CollaborativeFilter;
import com.kkumteul.domain.recommendation.filter.ContentBasedFilter;

import java.time.*;
import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private final AsyncService asyncService;

    // 성능 비교 테스트 코드 때문에 합치면 안됨
    @Transactional
    @Cacheable(value = "recommendations", key = "#childProfileId", unless = "#result == null")
    public List<RecommendBookDto> getRecommendationsWithCache(Long childProfileId) {
        return getRecommendedBooks(childProfileId); // 캐시가 없으면 DB 조회
    }

    //추천 도서 조회 - Redis에서 먼저 조회하고 없으면 DB에서 가져와 Redis에 저장
    public List<RecommendBookDto> getRecommendedBooks(Long childProfileId) {
        log.info("getRecommendedBooks - Input childProfileId: {}", childProfileId);
        List<Book> recommendBooks = recommendationRepository.findBookByChildProfileId(childProfileId)
                .orElseThrow(() -> new RecommendationBookNotFoundException(childProfileId));

        log.info("found recommendedBooks: {}", recommendBooks.size());

        if(recommendBooks.size() == 0){
            // 추천 도서 테이블에 없는 경우
            ChildDataDto childDataDto = getChildInfo(childProfileId)
                    .orElse(null); // 히스토리 없을 경우 null

            recommendBooks = getDefaultRecommendations(childDataDto);
            Collections.shuffle(recommendBooks);

            // 5권 저장
            recommendBooks = recommendBooks.subList(0, Math.min(5, recommendBooks.size()));

            // db에 저장
            saveRecommendations(childProfileId, recommendBooks);
        }

        return recommendBooks.stream()
                .map(RecommendBookDto::fromEntity)
                .toList();
    }

    // 좋아요 순 인기 도서 추천
    @Transactional(readOnly = true)
    public List<RecommendBookDto> getPopularRecommendations() {
        // 1. 좋아요 수 기준 상위 5개 도서 조회
        Pageable pageable = PageRequest.of(0, 5); // 상위 5개 도서만 가져오기
        List<Book> popularBooks = likeRepository.findTopBooksByLikes(pageable);

        // 2. 조회된 도서를 RecommendBookDto로 매핑
        List<RecommendBookDto> recommendBookDtos = popularBooks.stream()
                .map(RecommendBookDto::fromEntity)
                .collect(Collectors.toList());

        // 3. 매핑된 추천 도서 목록 반환
        return recommendBookDtos;
    }

    // 사용자별 맞춤 추천 도서 저장
    @Transactional
    public void saveRecommendations(Long userId, List<Book> recommendations) {
        // 1. 자녀 프로필 조회
        ChildProfile childProfile = childProfileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 2. 배치 저장을 위한 chunk 크기 정의
        int batchSize = 50;  // 한번에 50개씩 저장
        List<Recommendation> recommendationEntities = new ArrayList<>();

        recommendationRepository.deleteByChildProfileId(childProfile.getId());

        // 3. 추천 도서 리스트를 반복하며 Recommendation 엔티티 생성
        for (int i = 0; i < recommendations.size(); i++) {
            Book book = recommendations.get(i);

            Recommendation recommendation = Recommendation.builder()
                    .book(book)
                    .childProfile(childProfile)
                    .build();

            recommendationEntities.add(recommendation);

            // 4. 배치 저장: 일정 batchSize마다 저장 및 초기화
            if (recommendationEntities.size() == batchSize) {
                recommendationRepository.saveAll(recommendationEntities);
                recommendationEntities.clear();  // 영속성 컨텍스트 초기화
            }
        }

        // 5. 남아있는 데이터 저장
        if (!recommendationEntities.isEmpty()) {
            recommendationRepository.saveAll(recommendationEntities);
        }
    }

    public void updateLastActivity(Long childProfileId) {
        asyncService.updateLastActivity(childProfileId);  // 비동기 메서드 호출
    }

    // 최근 7일 내 활동한 childProfile id 리턴
    public List<Long> getActiveUserIds() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);
        return childProfileRepository.findActiveUserIdsLast7Days(threshold);
    }

    // 사용자 맞춤 추천 로직
    @Transactional(readOnly = true)
    public List<Book> getRecommendations(Long userId, List<BookDataDto> allBooks, List<ChildDataDto> childDataList) {

        // 1. 자녀 프로필 정보 조회
        ChildProfile childProfile = childProfileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 자녀 정보 가져오기
        Optional<ChildDataDto> childDataDto = getChildInfo(childProfile.getId());
        if(childDataDto.isEmpty()){
            // 신규 사용자거나 자녀 히스토리가 없는 경우
            List<Book> bookList = getDefaultRecommendations(null);
            Collections.shuffle(bookList);

            bookList = bookList.subList(0, Math.min(5, bookList.size())); // 5권 저장

            // db에 저장
            saveRecommendations(childProfile.getId(), bookList);

            return bookList;
        }

//        log.info("============사용자 ID " + userId + "의 추천 책 뽑기==============");
        // 4. 콘텐츠 기반 필터링 수행 - 결과를 bookId로 매핑
        Map<Long, Double> contentScores = contentBasedFilter.filterBooksByUserPreferences(childDataDto.get(), allBooks)
                .entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getBookId(), // key는 bookId로 설정
                        Map.Entry::getValue // value는 점수
                ));


        // 5. 협업 필터링 수행
        // 유사한 프로필 찾기
        List<ChildDataDto> similarProfiles = collaborativeFilter.findSimilarProfiles(childDataDto.get(), childDataList);

        // 협업 필터링 점수 계산 (bookId를 key로 사용)
        Map<Long, Double> collaborativeScores = getCollaborativeScores(similarProfiles, contentScores, userId);

        // 6. 콘텐츠 + 협업 필터링 점수 합산
        Map<BookDataDto, Double> finalScores = new HashMap<>();

        for (BookDataDto book : allBooks) {
            long bookId = book.getBookId();

            // 각 필터링 점수를 bookId로 가져옴
            double contentScore = contentScores.getOrDefault(bookId, 0.0);
            double collaborativeScore = collaborativeScores.getOrDefault(bookId, 0.0);

            // 최종 점수 계산: 콘텐츠와 협업 필터링 가중치 반영(콘텐츠 기반 0.55, 협업 필터링 0.45)
            double finalScore = (contentScore * 0.60) + (collaborativeScore * 0.40);

            // 최종 점수 설정 및 저장
            book.setScore(finalScore);

            // 최종 점수가 0 이상일 경우 - 콘텐츠 기반, 협업 필터링에서 한번도 가중치를 못받았으면 리스트 포함 x
            if(finalScore > 0) {
                finalScores.put(book, finalScore);
//                System.out.println(String.format("책: %s | 콘텐츠 점수: %f | 협업 점수: %f | 최종 점수: %f", book.getTitle(), contentScore, collaborativeScore, finalScore));

            }

        }

        // 7. 최종 추천 도서 필터링 (점수 상위 5개 뽑기)
        return finalRecommendedBooks(finalScores, childDataDto.get());
    }

    // 도서 정보 한꺼번에 가져오기 위해 ( + 데이터 dto로 매핑 )
    @Transactional(readOnly = true)
    public List<BookDataDto> getAllBookInfo(List<Book> books) {
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

            List<MBTIName> mbtiNameList = new ArrayList<>();
            for(BookMBTI bookMBTI : book.getBookMBTIS())
            {
                mbtiNameList.add(bookMBTI.getMbti().getMbti());
            }

            // BookDataDto
            BookDataDto bookDataDto = BookDataDto.builder()
                    .bookId(book.getId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .genreDto(new GenreDto(book.getGenre().getId(), book.getGenre().getName()))
                    .topics(topicDtos)
                    .mbti(mbtiNameList)
                    .build();

            bookDataDtos.add(bookDataDto);
        }

        return bookDataDtos;
    }

    // 모든 자녀 정보(성별, 생년월일, mbti) - 협업 필터링에 사용됨
    @Transactional(readOnly = true)
    public List<ChildDataDto> getChildrenInfo() {
        List<ChildPersonalityHistory> histories = historyRepository.findAllChildrenData();

        List<ChildDataDto> childDataAll = new ArrayList<>();

        for (ChildPersonalityHistory history : histories) {
            ChildDataDto dto = ChildDataDto.builder()
                    .id(history.getChildProfile().getId())
                    .gender(history.getChildProfile().getGender())
                    .birthDate(history.getChildProfile().getBirthDate())
                    .mbti(history.getMbtiScore().getMbti().getMbti())
                    .IScore(history.getMbtiScore().getIScore())
                    .EScore(history.getMbtiScore().getEScore())
                    .SScore(history.getMbtiScore().getSScore())
                    .NScore(history.getMbtiScore().getNScore())
                    .FScore(history.getMbtiScore().getFScore())
                    .TScore(history.getMbtiScore().getTScore())
                    .JScore(history.getMbtiScore().getJScore())
                    .PScore(history.getMbtiScore().getPScore())
                    .build();
            childDataAll.add(dto);
        }

        return childDataAll;
    }

    // 유사한 사용자가 좋아요 한 도서에 유사도 점수 추가
    public Map<Long, Double> getCollaborativeScores(List<ChildDataDto> similarProfiles, Map<Long, Double> initialScores, Long userId) {
        Pageable pageable = PageRequest.of(0, 20);
        Map<Long, Double> updatedScores = new HashMap<>(initialScores);

//        log.info("===========유사한 프로필들의 좋아요 도서를 조회하고 점수를 누적=========");

        // 1. 유사한 프로필들의 좋아요 도서를 조회하고 점수를 누적
        for (ChildDataDto profile : similarProfiles) {
            double similarityScore = profile.getScore(); // 프로필의 유사도 점수 가져오기

            // 유사한 사용자가 좋아한 도서 목록 조회
            Set<BookDataDto> likedBooks = getBooksList(Set.of(profile.getId()), pageable);

//            log.info("유사 프로필 ID: {} | 유사도 점수: {}", profile.getId(), similarityScore);

            // 도서별로 점수를 누적
            for (BookDataDto book : likedBooks) {
                long bookId = book.getBookId();
                double previousScore = updatedScores.getOrDefault(bookId, 0.0); // 기존 점수 가져오기

                // 가중 평균 계산 (기존 점수가 0이라도 유사도 반영) 유사도에 따라 비율 조정
                double weight = similarityScore / (similarityScore + 1); // 가중치 계산
                double newScore = previousScore * (1 - weight) + similarityScore * weight;

                updatedScores.put(bookId, newScore);
//                log.info("도서 ID: {} | 이전 점수: {} | 새 점수: {}", bookId, previousScore, newScore);
            }
        }

        return normalizeScores(updatedScores);
    }

    // 최종 추천
    private List<Book> finalRecommendedBooks(Map<BookDataDto, Double> finalScores, ChildDataDto childDataDto) {
        // 1. 상위 50개 추천 도서(BookDataDto)를 점수 기준으로 추출
        List<BookDataDto> topBookDtos = new ArrayList<>(finalScores.keySet());

        // 2. 점수 내림차순 정렬
        topBookDtos.sort((dto1, dto2) -> Double.compare(finalScores.get(dto2), finalScores.get(dto1)));

        // 3. 상위 50개 추출 (최대 50개만 가져오도록 조정)
        List<BookDataDto> top50Books = topBookDtos.subList(0, Math.min(50, topBookDtos.size()));

        // 4. 50개 중 랜덤으로 5개 선택
        Collections.shuffle(top50Books); // 무작위로 섞기
        List<BookDataDto> selectedBookDtos = top50Books.subList(0, Math.min(5, top50Books.size()));

        // 5. DTO를 엔티티로 변환
        List<Book> recommendedBooks = convertToBookEntities(selectedBookDtos);

        // 6. Fallback: 추천 도서가 5개 미만일 경우 기본 추천 목록으로 채우기
        if (recommendedBooks.size() < 5) {
            List<Book> defaultRecommendations = getDefaultRecommendations(childDataDto); // 기본 추천 목록
            int remaining = 5 - recommendedBooks.size();
            recommendedBooks.addAll(defaultRecommendations.subList(0, Math.min(remaining, defaultRecommendations.size())));
        }

        return recommendedBooks;
    }

    // dto를 엔티티로
    private List<Book> convertToBookEntities(List<BookDataDto> bookDataDtos) {
        List<Book> books = new ArrayList<>();

        for (BookDataDto dto : bookDataDtos) {
            Book book = bookRepository.findById(dto.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다: " + dto.getBookId()));
            books.add(book);
        }

        return books;
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


    // 자녀 정보 한꺼번에 가져오기(한명)
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
                .IScore(history.getMbtiScore().getIScore())
                .EScore(history.getMbtiScore().getEScore())
                .SScore(history.getMbtiScore().getSScore())
                .NScore(history.getMbtiScore().getNScore())
                .FScore(history.getMbtiScore().getFScore())
                .TScore(history.getMbtiScore().getTScore())
                .JScore(history.getMbtiScore().getJScore())
                .PScore(history.getMbtiScore().getPScore())
                .build();

        return Optional.of(childDataDto);
    }

    // 모든 점수를 [0, 1]로 정규화
    private Map<Long, Double> normalizeScores(Map<Long, Double> scores) {
        double maxScore = scores.values().stream().max(Double::compare).orElse(1.0);

        return scores.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() / maxScore
                ));
    }

    // 기본 추천 목록 - 사실 가중치 점수 때문에 나이, 성별로 추천 되는 책이 있어서... 여기까지 갈 일은 없겠지만 그냥 책 추천 연령대랑 나이차 가장 적은 순으로(ex. 10세부터면 10~15살)
    private List<Book> getDefaultRecommendations(ChildDataDto childDataDto){
        int age = getAge(childDataDto.getBirthDate());

        Pageable pageable = PageRequest.of(0, 50);
        return bookRepository.findBookListByAgeGroup(age, pageable);
    }

    // 생년월일을 나이로 변환하는 메서드
    private int getAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }


}

