package org.maping.maping.api.auth.service;

import lombok.RequiredArgsConstructor;
import org.maping.maping.common.utills.ULID.ULIDUtill;
import org.maping.maping.common.utills.users.oauth.google.dto.GoogleUserInfoResponse;
import org.maping.maping.common.utills.users.oauth.naver.NaverUtil;
import org.maping.maping.external.oauth.naver.dto.response.NaverUserInfoResponse;
import org.maping.maping.repository.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.maping.maping.api.auth.dto.response.OAuthLoginResponse;
import org.maping.maping.common.enums.expection.ErrorCode;
import org.maping.maping.common.utills.jwt.JWTUtill;
import org.maping.maping.common.utills.jwt.dto.JwtDto;
import org.maping.maping.exceptions.CustomException;
import org.maping.maping.model.user.LocalJpaEntity;
import org.maping.maping.model.user.UserInfoJpaEntity;
import org.maping.maping.repository.user.LocalRepository;
import org.maping.maping.common.utills.users.oauth.google.GoogleUtil;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {
    private final GoogleUtil googleUtil;
    private final NaverUtil naverUtil;
    private final JWTUtill jwtUtil;
    private final UserRepository userRepository;

    @Override
    public OAuthLoginResponse naverLogin(String code, String state) {
        // Naver에서 사용자 정보를 가져옵니다.
        String accessToken = naverUtil.getAccessToken(code, state);
        NaverUserInfoResponse userInfo = naverUtil.getUserInfo(accessToken);

        Optional<UserInfoJpaEntity> optionalUser = userRepository.findByEmail(userInfo.getResponse().getEmail());

        // 이미 가입된 회원이라면 JWT 반환
        if (optionalUser.isPresent()) {
            JwtDto jwtDto = jwtUtil.generateJwtDto(String.valueOf(optionalUser.get().getUserId()), null);
            return OAuthLoginResponse.builder()
                    .isNewMember(false)
                    .jwtDto(jwtDto)
                    .build();
        }

        // 신규 회원 처리
        UserInfoJpaEntity user = UserInfoJpaEntity.builder()
                .email(userInfo.getResponse().getEmail())
                .userName(userInfo.getResponse().getName())
                .iconic(null) // 프로필 이미지
                .naver(null) // NaverJpaEntity는 나중에 추가해야 할 경우
                .local(null) // LocalJpaEntity는 나중에 추가해야 할 경우
                .build();

        userRepository.save(user); // user_id는 자동 증가하므로 명시적으로 설정할 필요 없음

        JwtDto jwtDto = jwtUtil.generateJwtDto(String.valueOf(user.getUserId()), null);
        return OAuthLoginResponse.builder()
                .isNewMember(true)
                .jwtDto(jwtDto)
                .build();
    }

        @Override
        public OAuthLoginResponse googleLogin(String code) {
            // Google에서 사용자 정보를 가져옵니다.
            String accessToken = googleUtil.getAccessToken(code);
            GoogleUserInfoResponse userInfo = googleUtil.getUserInfo(accessToken);

            return processOAuthLogin(userInfo.getEmail(), userInfo.getName(), userInfo.getPicture());
        }

        private OAuthLoginResponse processOAuthLogin(String email, String name, String profileImage) {
            Optional<UserInfoJpaEntity> optionalUser = userRepository.findByEmail(email);

            // 이미 가입된 회원이라면 JWT 반환
            if (optionalUser.isPresent()) {
                JwtDto jwtDto = jwtUtil.generateJwtDto(String.valueOf(optionalUser.get().getUserId()), null);
                return OAuthLoginResponse.builder()
                        .isNewMember(false)
                        .jwtDto(jwtDto)
                        .build();
            }

            // 신규 회원 처리
            UserInfoJpaEntity user = UserInfoJpaEntity.builder()
                    .email(email)
                    .userName(name)
                    .iconic(null) // Google은 프로필 이미지 제공
                    .naver(null)
                    .local(null)
                    .build();

            userRepository.save(user);

            JwtDto jwtDto = jwtUtil.generateJwtDto(String.valueOf(user.getUserId()), null);
            return OAuthLoginResponse.builder()
                    .isNewMember(true)
                    .jwtDto(jwtDto)
                    .build();
        }

}
