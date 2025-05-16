package com.kkumteul.domain.user.repository;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.Gender;
import com.kkumteul.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private static User user;
    private static ChildProfile childProfile;

    @BeforeEach
    void setup() {
        user = User.builder()
                .username("name")
                .password("password")
                .nickName("nickname")
                .birthDate(new Date())
                .phoneNumber("01012345678")
                .build();

        childProfile = ChildProfile.builder()
                .name("childName")
                .birthDate(new Date())
                .gender(Gender.FEMALE)
                .build();
    }

    @Test
    @DisplayName("유저 저장 테스트")
//    @Rollback(false)
    void save() {
        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
    }

    @Test
    @DisplayName("유저 조회 테스트")
    void getUser() {
        //given
        User saveUser = userRepository.save(user);

        //when
        Optional<User> user = userRepository.findByIdWithChildProfiles(saveUser.getId());

        //then
        assertThat(user).isNotEmpty();
    }

    @Test
    @DisplayName("유저와 자녀 정보 저장 및 조회 테스트")
    void save_and_getUserWithChildren() {
        //given
        user.getChildProfileList().add(childProfile);
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        //when
        Optional<User> user = userRepository.findByIdWithChildProfiles(userId);

        //then
        assertThat(user).isPresent();
        assertThat(user.get().getChildProfileList()).isNotEmpty();
        assertThat(user.get().getChildProfileList().get(0).getName()).isEqualTo("childName");
    }

}