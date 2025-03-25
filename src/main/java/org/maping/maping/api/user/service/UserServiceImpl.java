package org.maping.maping.api.user.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maping.maping.api.auth.dto.request.PasswordRequest;
import org.maping.maping.api.user.dto.request.UserApiRequest;
import org.maping.maping.api.user.dto.response.UserInfoResponse;
import org.maping.maping.common.response.BaseResponse;
import org.maping.maping.common.utills.ULID.ULIDUtill;
import org.maping.maping.common.utills.users.oauth.google.dto.GoogleUserInfoResponse;
import org.maping.maping.common.utills.users.oauth.naver.NaverUtil;
import org.maping.maping.external.nexon.NEXONUtils;
import org.maping.maping.external.nexon.dto.character.CharacterListDto;
import org.maping.maping.external.oauth.naver.dto.response.NaverUserInfoResponse;
import org.maping.maping.model.user.UserApiJpaEntity;
import org.maping.maping.repository.user.UserApiRepository;
import org.maping.maping.repository.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import org.springframework.transaction.annotation.Isolation;

import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LocalRepository localRepository;
    private final UserApiRepository userApiRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final NEXONUtils nexonUtils;

    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-=]{6,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private static final String NICKNAME_REGEX = "^[가-힣a-zA-Z0-9]{2,10}$";
    private static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEX);

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        UserInfoJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "사용자를 찾을 수 없습니다."));

        return new UserInfoResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getUserApi() != null ? user.getUserApi().getUserApiInfo() : null
        );
    }

    @Transactional
    @Override
    public void updateNickname(Long userId, String newNickname) {
        // 닉네임 유효성 검사
        if (newNickname == null || !NICKNAME_PATTERN.matcher(newNickname).matches()) {
            throw new CustomException(ErrorCode.BadRequest, "닉네임은 한글, 영어, 숫자로만 2자 이상 10자 이하로 입력해야 합니다.");
        }

        // 사용자 조회
        UserInfoJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "사용자를 찾을 수 없습니다."));

        // 닉네임 중복 체크
        if (userRepository.existsByuserName(newNickname)) {
            throw new CustomException(ErrorCode.BadRequest, "이미 사용 중인 닉네임입니다.");
        }

        // 닉네임 업데이트
        user.setUserName(newNickname);
        userRepository.save(user);
    }

        @Transactional
        @Override
        public void updatePassword(Long userId, PasswordRequest passwordRequest) {
            // 사용자 조회
            LocalJpaEntity local = localRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "사용자를 찾을 수 없습니다."));

            // 비밀번호 유효성 검사
            if (passwordRequest.getPassword() == null || !PASSWORD_PATTERN.matcher(passwordRequest.getPassword()).matches()) {
                throw new CustomException(ErrorCode.BadRequest, "비밀번호는 영문, 숫자를 포함한 6자 이상이어야 합니다.");
            }

            // 새로운 비밀번호로 업데이트
            local.setPassword(passwordEncoder.encode(passwordRequest.getPassword()));

            // 저장
            localRepository.save(local);
        }

    @Transactional
    public void postUserApi(Long userId, UserApiRequest request) {
        UserInfoJpaEntity userInfo = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserApiJpaEntity userApi = UserApiJpaEntity.builder()
                .id(userId)
                .userInfoTb(userInfo)
                .userApiInfo(request.getUserApiInfo())
                .build();

        userApiRepository.save(userApi);
    }

    @Transactional
    public void deleteUserApi(Long userId) {
        UserApiJpaEntity userApi = userApiRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "User API not found"));

        userApiRepository.delete(userApi);
    }

    @Override
    public BaseResponse<String> setOriginalCharacter(Long userId, String ocid) {
        Optional<UserApiJpaEntity> apiKey = userApiRepository.findById(userId);
        if (apiKey.isPresent()) {
            if (apiKey.get().getUserApiInfo() == null) {
                throw new CustomException(ErrorCode.BadRequest, "API 키가 존재하지 않습니다.");
            }
        } else {
            throw new CustomException(ErrorCode.BadRequest, "API 키가 존재하지 않습니다.");
        }
        CharacterListDto characterList = nexonUtils.getCharacterList(apiKey.get().getUserApiInfo());
        log.info(String.valueOf(characterList));
        if (characterList != null) {
            for(int i = 0; i < characterList.getAccountList().size(); i++){
                if(characterList.getAccountList().get(i).getCharacterList().getFirst().getOcid().equals(ocid)){
                    UserInfoJpaEntity user = userRepository.findById(userId)
                            .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "사용자를 찾을 수 없습니다."));
                    user.setMainCharacterOcid(ocid);
                    user.setMainCharacterName(characterList.getAccountList().get(i).getCharacterList().getFirst().getCharacterName());
                    userRepository.save(user);
                    return new BaseResponse<>(200, "본캐 설정 성공", "본캐 설정 성공", true);
                }
            }
        }else{
            return new BaseResponse<>(404, "유효하지 않은 OCID입니다.", "유효하지 않은 OCID입니다.", false);
        }
        return new BaseResponse<>(400, "본캐 설정 실패", "본캐 설정 실패", false);
    }
}
