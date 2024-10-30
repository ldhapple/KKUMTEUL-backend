package com.kkumteul.domain.mbti.entity;

public enum MBTIName {

    ISTJ, ESTJ, INTJ, ENTJ,
    ISFJ, ESFJ, INFJ, ENFJ,
    ISTP, ESTP, INTP, ENTP,
    ISFP, ESFP, INFP, ENFP;

    public static MBTIName fromString(String mbtiString) {
        for (MBTIName mbti : MBTIName.values()) {
            if (mbti.name().equalsIgnoreCase(mbtiString)) {
                return mbti;
            }
        }

        throw new IllegalArgumentException("Invalid MBTI name: " + mbtiString);
    }
}
