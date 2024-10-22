package com.kkumteul.domain.recommendation.filter;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.recommendation.dto.ChildDataDto;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimilarityCalculator {

    // 유사도 계산
    public double calculateSimilarity(ChildDataDto target, ChildDataDto other) {
        double similarity = 0.0;

        // 1. 나이 비교 (10살 이하 차이일 경우 0.3 가중치)
        int ageDifference = Math.abs(getAge(target.getBirthDate()) - getAge(other.getBirthDate()));
        if (ageDifference <= 10) {
            similarity += 0.3;
        }

        // 2. 성별 비교 (같으면 0.3 가중치)
        if (target.getGender().equals(other.getGender())) {
            similarity += 0.3;
        }

        // 3. MBTI 비교 (같으면 0.4 가중치)
        if (target.getMbti().equals(other.getMbti())) {
            similarity += 0.4;
        }

        return similarity; // 최종 유사도 반환
    }

    // 코사인 유사도 계산 - 활용 안함
    public double cosineSimilarity(List<String> genres1, List<String> genres2) {
        if (genres1.isEmpty() || genres2.isEmpty()) {
            return 0.0;
        }

        Set<String> uniqueGenres = new HashSet<>(genres1);
        uniqueGenres.addAll(genres2);

        int[] vector1 = new int[uniqueGenres.size()];
        int[] vector2 = new int[uniqueGenres.size()];

        int index = 0;
        for (String genre : uniqueGenres) {
            vector1[index] = genres1.contains(genre) ? 1 : 0;
            vector2[index] = genres2.contains(genre) ? 1 : 0;
            index++;
        }

        return dotProduct(vector1, vector2) / (magnitude(vector1) * magnitude(vector2));
    }

    private double dotProduct(int[] vector1, int[] vector2) {
        int sum = 0;
        for (int i = 0; i < vector1.length; i++) {
            sum += vector1[i] * vector2[i];
        }
        return sum;
    }

    private double magnitude(int[] vector) {
        int sum = 0;
        for (int value : vector) {
            sum += value * value;
        }
        return Math.sqrt(sum);
    }

    // 생년월일을 나이로 변환하는 메서드
    private int getAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
