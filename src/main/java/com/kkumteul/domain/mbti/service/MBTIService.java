package com.kkumteul.domain.mbti.service;

import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.repository.MBTIRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
//@RequiredArgsConstructor
@AllArgsConstructor
@Transactional
public class MBTIService {
    private MBTIRepository MbtiRepository;

    public MBTI insertMBTI(MBTI mbti) {
        return MbtiRepository.save(mbti);
    }
}
