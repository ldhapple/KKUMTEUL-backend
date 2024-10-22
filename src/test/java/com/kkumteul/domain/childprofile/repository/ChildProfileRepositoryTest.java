package com.kkumteul.domain.childprofile.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.Gender;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.entity.User.UserBuilder;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ChildProfileRepositoryTest {

    @Autowired
    private ChildProfileRepository childProfileRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    private User user;
    private ChildProfile childProfile2;
    
    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("user")
                .password("1234")
                .build();
        
        entityManager.persist(user);

        ChildProfile childProfile1 = ChildProfile.builder()
                .name("lee")
                .gender(Gender.FEMALE)
                .user(user)
                .build();

        childProfile2 = ChildProfile.builder()
                .name("lee")
                .gender(Gender.FEMALE)
                .user(user)
                .build();

        entityManager.persist(childProfile1);
        entityManager.persist(childProfile2);
    }

    @Test
    @DisplayName("유저 아이디로 자녀 프로필 조회 성공 테스트")
    void testFindByUserId() {
        Long userId = user.getId();

        Optional<List<ChildProfile>> findProfiles = childProfileRepository.findByUserId(userId);

        assertThat(findProfiles).isPresent();
        assertThat(findProfiles.get()).hasSize(2);
        assertThat(findProfiles.get().get(1)).isEqualTo(childProfile2);
    }

    @Test
    @DisplayName("조회된 자녀 프로필이 없을 경우 테스트")
    void testChildProfileNotFound() {
        User userHasNotChildProfile = User.builder().build();

        entityManager.persist(userHasNotChildProfile);

        Optional<List<ChildProfile>> findChildProfiles = childProfileRepository.findByUserId(userHasNotChildProfile.getId());

        assertThat(findChildProfiles.get()).isEmpty();
    }

    @Test
    @DisplayName("자녀 프로필 존재 여부 확인 테스트")
    void testFindById() {
        Optional<ChildProfile> findChildProfile = childProfileRepository.findById(childProfile2.getId());

        assertThat(findChildProfile).isPresent();
        assertThat(findChildProfile.get().getName()).isEqualTo(childProfile2.getName());
    }
}