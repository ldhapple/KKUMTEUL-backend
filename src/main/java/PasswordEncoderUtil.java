import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // 비밀번호를 입력.
        String plainPassword = "yourPassword"; // 여기서 "yourPassword"를 원하는 비밀번호로 변경
        String encodedPassword = passwordEncoder.encode(plainPassword);

        // 비밀번호 출력
        System.out.println("Encoded password: " + encodedPassword);
    }
}