package com.kkumteul.domain.childprofile.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.GenreScore;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.repository.GenreRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class GenreScoreRepositoryTest {

    @Autowired
    private GenreScoreRepository genreScoreRepository;

    @Autowired
    private ChildProfileRepository childProfileRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    @DisplayName("자녀 프로필 ID로 장르 점수 조회 테스트")
    void testFindByChildProfileId() {
        ChildProfile childProfile = childProfileRepository.save(ChildProfile.builder()
                .name("lee")
                .build());

        Genre genre = genreRepository.save(Genre.builder()
                .name("그림책")
                .build());

        GenreScore genreScore = GenreScore.builder()
                .genre(genre)
                .score(5.0)
                .build();

        genreScore.setChildProfile(childProfile);

        genreScoreRepository.save(genreScore);

        List<GenreScore> foundGenreScores = genreScoreRepository.findByChildProfileId(childProfile.getId());

        assertThat(foundGenreScores).isNotEmpty();
        assertThat(foundGenreScores.get(0).getScore()).isEqualTo(5.0);
        assertThat(foundGenreScores.get(0).getGenre().getName()).isEqualTo("그림책");
    }
}