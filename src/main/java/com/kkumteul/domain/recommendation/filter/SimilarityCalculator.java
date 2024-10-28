package com.kkumteul.domain.recommendation.filter;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.recommendation.dto.ChildDataDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SimilarityCalculator {

    // 전체 유사도 계산 메서드
    public double calculateSimilarity(ChildDataDto target, ChildDataDto other) {
        double similarity = 0.0;

        // 1. 나이 비교 (10살 이하 차이일 경우 0.3 가중치)
        double ageDifference = calculateDimensionWeight(getAge(target.getBirthDate()), getAge(other.getBirthDate()));
        if (ageDifference > 10) {
            similarity = 0.0; // 10살 이상의 차이일 경우 가중치 0
        } else {
            similarity += (10 - ageDifference) / 10.0 * 0.3; // 나이 차이가 작을수록 0.3에 가까워짐
        }

        // 2. 성별 비교 (같으면 0.3 가중치)
        if (target.getGender().equals(other.getGender())) {
            similarity += 0.1;
        }

        // 3. MBTI 점수 비교 (최대 0.6 가중치)
        similarity += calculateMbtiSimilarity(target, other);

        return similarity;  // 최종 유사도 반환
    }

    // MBTI 점수 유사도 계산 메서드
    private double calculateMbtiSimilarity(ChildDataDto target, ChildDataDto other) {
        double totalWeight = 0.0;

        // I/E 비교
        totalWeight += calculateDimensionWeight(target.getIScore(), other.getIScore());

        // S/N 비교
        totalWeight += calculateDimensionWeight(target.getSScore(), other.getSScore());

        // T/F 비교
        totalWeight += calculateDimensionWeight(target.getTScore(), other.getTScore());

        // J/P 비교
        totalWeight += calculateDimensionWeight(target.getJScore(), other.getJScore());

        // 최대 0.6 가중치를 부여
        return totalWeight / 4 * 0.6;
    }

    // 점수 차이가 작을 수록 더 많은 유사도 점수를 가짐
    private double calculateDimensionWeight(double targetScore, double otherScore) {
        double difference = Math.abs(targetScore - otherScore);  // 오차 계산
        return 1 - (difference / 100.0);  // 오차를 기반으로 가중치 계산
    }


    // 코사인 유사도 계산 - 콘텐츠 기반 필터링(사용자의 선호 장르, 책 장르 등 비교)
    public double cosineSimilarity(List<String> list1, List<String> list2) {
        if (list1.isEmpty() || list2.isEmpty()) {
            return 0.0; // 빈 리스트일 경우 유사도 0 반환
        }

        // 두 리스트의 고유한 요소들을 합집합으로 만듦
        Set<String> uniqueElements = new HashSet<>(list1);
        uniqueElements.addAll(list2);

        // 두 리스트를 벡터로 변환
        int[] vector1 = new int[uniqueElements.size()];
        int[] vector2 = new int[uniqueElements.size()];

        int index = 0;
        for (String element : uniqueElements) {
            vector1[index] = list1.contains(element) ? 1 : 0;
            vector2[index] = list2.contains(element) ? 1 : 0;
            index++;
        }

        // 코사인 유사도 계산
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
