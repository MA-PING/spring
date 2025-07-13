package org.maping.maping.api.auth.service;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.maping.maping.api.auth.dto.request.NicknameCheckRequest;
import org.maping.maping.api.auth.dto.request.UserRegistrationRequest;
import org.maping.maping.common.enums.expection.ErrorCode;
import org.maping.maping.common.utills.jwt.JWTUtill;
import org.maping.maping.common.utills.jwt.dto.JwtDto;
import org.maping.maping.exceptions.CustomException;
import org.maping.maping.model.user.LocalJpaEntity;
import org.maping.maping.model.user.UserInfoJpaEntity;
import org.maping.maping.repository.user.LocalRepository;
import org.maping.maping.repository.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.regex.Pattern;

@Service
@Builder
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final LocalRepository localJpaRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JWTUtill jwtUtil;


    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-=]{6,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private static final String NICKNAME_REGEX = "^[가-힣a-zA-Z0-9]{2,10}$";
    private static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEX);


    @Override
    public boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    private void checkValidNickname(String nickname) {
        if (!isValidNickname(nickname)) {
            throw new IllegalArgumentException("닉네임 형식이 유효하지 않습니다.");
        }
    }

    @Override
    public boolean isValidNickname(String nickname) {
        if (nickname == null) {
            return false;
        }
        return NICKNAME_PATTERN.matcher(nickname).matches();
    }

    @Override
    public boolean isDuplicateNickname(NicknameCheckRequest request) {
        String nickname = request.getNickname();

        // 닉네임 유효성 검사
        if (!isValidNickname(nickname)) {
            throw new CustomException(ErrorCode.BadRequest, "닉네임 형식이 유효하지 않습니다.");
        }
        // 닉네임 중복 여부 확인
        return userRepository.existsByuserName(nickname);
    }



    @Override
    public void registerUser(UserRegistrationRequest registrationDto) {
        // 사용자 정보 엔티티 생성
        UserInfoJpaEntity userInfo = UserInfoJpaEntity.builder()
                .email(registrationDto.getEmail())
                .userName(registrationDto.getUserName())
                .build();

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());

        // 로컬 엔티티 생성
        LocalJpaEntity local = LocalJpaEntity.builder()
                .email(registrationDto.getEmail())
                .password(encodedPassword)
                .userInfo(userInfo)
                .build();

        // 데이터베이스에 저장
        userRepository.save(userInfo);
        localJpaRepository.save(local);
    }

    @Override
    public JwtDto login(String email, String password) {
        // 이메일로 LocalJpaEntity 사용자 조회
        LocalJpaEntity local = localJpaRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // UserInfoJpaEntity에서 유저 네임 가져오기
        UserInfoJpaEntity userInfo = local.getUserInfo();

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, local.getPassword())) {
            throw new CustomException(ErrorCode.Unauthorized, "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        // JWT 생성 후 반환 (유저 ID를 String으로 변환)
        return jwtUtil.generateJwtDto(String.valueOf(userInfo.getUserId()), userInfo.getUserName());
    }

    @Override
    public JwtDto reissue(String refreshToken) {
        if (jwtUtil.isValidRefreshToken(refreshToken)) {
            String userId = jwtUtil.getClaims(refreshToken).getSubject();


            // 기본 role을 직접 지정 (ex. "USER")
            return jwtUtil.generateJwtDto(userId, "USER");
        } else {
            throw new CustomException(ErrorCode.BadRequest, "올바른 Refresh 토큰이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
