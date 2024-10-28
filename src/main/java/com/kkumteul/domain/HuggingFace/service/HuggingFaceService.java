package com.kkumteul.domain.HuggingFace.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkumteul.domain.HuggingFace.dto.HuggingRequestDto;
import com.kkumteul.domain.HuggingFace.exception.DuplicateBookException;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookMBTI;
import com.kkumteul.domain.book.repository.BookRepository;
import com.kkumteul.domain.book.repository.BookMBTIRepository;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.entity.MBTIName;
import com.kkumteul.domain.mbti.repository.MBTIRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HuggingFaceService {
    @Value("${huggingface.api.token}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final BookRepository bookRepository;
    private final MBTIRepository mbtiRepository;
    private final BookMBTIRepository bookMbtiRepository;

    // 모든 도서들 mbti 연결
    public void updateBooksLinkMbti() {
        List<Book> books = bookRepository.findAll();

        for (Book book : books) {
            HuggingRequestDto request = new HuggingRequestDto(book.getTitle(), book.getSummary(), book.getId());

            // MBTI 분석 및 책 업데이트
            try {
                String mbtiType = analyzeText(request);
                linkMbtiToBook(book, mbtiType);
                System.out.println("도서의 MBTI를 추가했습니다.: " + book.getTitle());
            } catch (Exception e) {
                System.err.println("Error processing book: " + book.getTitle());
                e.printStackTrace();
            }
        }
    }

    // MBTI 분석 후 주어진 bookId에 MBTI 매핑
    public String newBookLinkMbti(HuggingRequestDto request) {
        try {
            // MBTI 분석
            String mbtiType = analyzeText(request);
            if (mbtiType != null && !mbtiType.isEmpty()) {
                // Book ID를 사용하여 MBTI를 연결
                Book book = bookRepository.findById(request.getBookId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 도서를 찾을 수 없습니다."));

                linkMbtiToBook(book, mbtiType);

                return "도서의 MBTI를 추가했습니다.: " + book.getTitle();
            } else {
                return "MBTI 유형을 분석할 수 없습니다.";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    public String analyzeText(HuggingRequestDto request) {
        String url = "https://api-inference.huggingface.co/models/JanSt/albert-base-v2_mbti-classification";
        HttpEntity<String> requestEntity = createRequestEntity(request);

        // API 호출 및 응답 처리
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        // 응답 본문에서 MBTI 유형 추출
        return extractMbtiFromResponse(responseEntity.getBody());
    }

    private void linkMbtiToBook(Book book, String mbtiType) {
        MBTIName typeEnum = MBTIName.valueOf(mbtiType);

        // MBTI 엔티티 조회
        MBTI mbti = mbtiRepository.findByMbti(typeEnum)
                .orElseThrow(() -> new IllegalArgumentException("해당 MBTI 유형이 존재하지 않습니다."));

        // Book과 MBTI 연결
        BookMBTI bookMbti = BookMBTI.builder()
                .book(book)
                .mbti(mbti)
                .build();

        try {
            // BookMBTI 저장
            bookMbtiRepository.save(bookMbti);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateBookException("이미 존재하는 도서입니다."); // 사용자 정의 예외 발생
        }
    }

    private HttpEntity<String> createRequestEntity(HuggingRequestDto request) {
        String prompt = String.format("제목이 '%s' 이고 줄거리가 '%s'인 이 책의 16가지의 MBTI 중 하나의 성향을 추천해 주세요.",
                request.getTitle(), request.getSummary());

        String jsonRequest = String.format("{\"inputs\": \"%s\"}", prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        return new HttpEntity<>(jsonRequest, headers);
    }

    private String extractMbtiFromResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode labelsNode = jsonNode.get(0);

            List<MbtiScore> scores = new ArrayList<>();

            for (JsonNode labelNode : labelsNode) {
                String label = labelNode.get("label").asText();
                double score = labelNode.get("score").asDouble();
                scores.add(new MbtiScore(label, score));
            }

            scores.forEach(score -> System.out.println(score.getLabel() + " Score: " + score.getScore()));

            return determineBestType(scores);
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    private String determineBestType(List<MbtiScore> scores) {
        double maxScore = 0.0;
        List<String> bestTypes = new ArrayList<>();

        for (MbtiScore score : scores) {
            if (score.getScore() > maxScore) {
                maxScore = score.getScore();
                bestTypes.clear();
                bestTypes.add(score.getLabel());
            } else if (score.getScore() == maxScore) {
                bestTypes.add(score.getLabel());
            }
        }

        return String.join("/", bestTypes);
    }

    public static class MbtiScore {
        private final String label;
        private final double score;

        public MbtiScore(String label, double score) {
            this.label = label;
            this.score = score;
        }

        public String getLabel() {
            return label;
        }

        public double getScore() {
            return score;
        }
    }
}
