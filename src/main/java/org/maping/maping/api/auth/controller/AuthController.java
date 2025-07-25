package org.maping.maping.api.auth.controller;

import groovy.util.logging.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.maping.maping.api.auth.dto.request.*;
import org.maping.maping.api.auth.dto.response.OAuthLoginResponse;
import org.maping.maping.api.auth.service.AuthService;
import org.maping.maping.api.auth.service.MailService;
import org.maping.maping.api.auth.service.OAuthService;
import org.maping.maping.common.enums.expection.ErrorCode;
import org.maping.maping.common.utills.jwt.JWTUtill;
import org.maping.maping.common.utills.jwt.dto.JwtDto;
import org.maping.maping.common.utills.redis.JwtRedisService;
import org.maping.maping.exceptions.CustomException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.mail.MessagingException;
import org.maping.maping.common.response.BaseResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Slf4j
@RestController
@Tag(name = "Auth", description = "Auth API")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final OAuthService oAuthService;
    private final MailService mailService;
    private final AuthService authService;
    private final JwtRedisService jwtRedisService;
    private final JWTUtill jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Operation(summary = "회원가입", description = "사용자를 등록하는 API")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public BaseResponse<String> registerUser(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        authService.registerUser(registrationRequest);
        return new BaseResponse<>(HttpStatus.CREATED.value(), "회원가입이 완료되었습니다.", "회원가입 성공", true);
    }

    @Operation(summary = "로그인", description = "사용자가 로그인하여 JWT를 발급받는 API")
    @PostMapping("/login")
    public BaseResponse<JwtDto> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        JwtDto jwtDto = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

        String accessToken = jwtDto.getAccessToken();
        String refreshToken = jwtDto.getRefreshToken();

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);

        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
//        accessTokenCookie.setDomain(".ma-ping.com");
        accessTokenCookie.setMaxAge(60 * 60 * 24);
        response.addCookie(accessTokenCookie);

        Duration refreshTokenDuration = Duration.ofDays(28);
        jwtRedisService.saveRefreshToken(
                accessToken,
                refreshToken,
                refreshTokenDuration
        );
        return new BaseResponse<>(HttpStatus.OK.value(), "로그인 성공", jwtDto, true);
    }

    @Operation(summary = "토큰 재발급", description = "리프레쉬 토큰 기반 액세스 토큰을 재발급 받는 API")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/reissue")
    public BaseResponse<JwtDto> reissue(@RequestBody ReissueRequest request) {
        boolean black =  jwtRedisService.isAccessTokenBlacklisted(request.getAccessToken());

        if(black) {
            return new BaseResponse<>(404, "블랙리스트에 있는 토큰입니다. 다시 로그인해주세요.", null, false);
        } else{
            String redisRefreshToken = jwtRedisService.getRefreshToken(request.getAccessToken());
            JwtDto newTokens = authService.reissue(redisRefreshToken);
            String accessToken = newTokens.getAccessToken();
            String refreshToken = newTokens.getRefreshToken();
            Duration refreshTokenDuration = Duration.ofDays(28);
            jwtRedisService.saveRefreshToken(
                    accessToken,
                    refreshToken,
                    refreshTokenDuration
            );
            return new BaseResponse<>(HttpStatus.OK.value(), "재발급 성공", newTokens, true);
        }
    }

    @Operation(summary = "로그아웃", description = "사용자가 로그아웃하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/logout")
    public BaseResponse<String> logout(@RequestBody ReissueRequest request, HttpServletRequest response) {
        try {
            Long userId = Long.valueOf(jwtUtil.getUserId(response));
            Long jwtUserId = Long.valueOf(jwtUtil.getUserId(request.getAccessToken()));
            if (userId.equals(jwtUserId)) {
                jwtRedisService.addAccessTokenToBlacklist(request.getAccessToken(), Duration.ofDays(28));
                jwtRedisService.deleteRefreshToken(request.getAccessToken());
                return new BaseResponse<>(HttpStatus.OK.value(), "로그아웃 성공", "로그아웃 성공", true);
            }

        } catch (Exception e) {
            logger.error("로그아웃 실패: {}", e.getMessage(), e);
            return new BaseResponse<>(404, "로그아웃 실패", "로그아웃 처리 중 오류가 발생했습니다.", false);
        }
        return new BaseResponse<>(404, "로그아웃 실패", "로그아웃 처리 중 오류가 발생했습니다.", false);
    }

    @Operation(summary = "네이버 로그인", description = "네이버 로그인 API")
    @GetMapping("/signup/naver")
    public void naverLogin(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpServletResponse response
    ) throws IOException {
        OAuthLoginResponse loginResponse = oAuthService.naverLogin(code, state);

        // accessToken 추출
        String accessToken = loginResponse.getJwtDto().getAccessToken();
        boolean isNew = loginResponse.isNewMember();

        // HttpOnly 쿠키로 accessToken 저장 (도메인: .ma-ping.com)
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // HTTPS 환경에서만 전송
        accessTokenCookie.setPath("/");
        accessTokenCookie.setDomain(".ma-ping.com"); // 도메인 전체에서 사용
        accessTokenCookie.setMaxAge(60 * 60 * 24); // 1일

        response.addCookie(accessTokenCookie);

        // 프론트엔드로는 신규가입여부만 쿼리로 전달
        String frontendRedirect = "https://ma-ping.com/social-callback?isNew=" + isNew;
        response.sendRedirect(frontendRedirect);
    }

    @Operation(summary = "구글 로그인", description = "구글 로그인 API")
    @GetMapping("/signup/google")
    public void googleLogin(
            @Parameter(description = "구글 요청을 통해 받아온 엑세스 토큰")
            @RequestParam("code") String code,
            HttpServletResponse response
    ) throws IOException {
        OAuthLoginResponse loginResponse = oAuthService.googleLogin(code);

        String accessToken = loginResponse.getJwtDto().getAccessToken();
        boolean isNew = loginResponse.isNewMember();

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setDomain(".ma-ping.com");
        accessTokenCookie.setMaxAge(60 * 60 * 24);

        response.addCookie(accessTokenCookie);

        String frontendRedirect = "https://ma-ping.com/social-callback?isNew=" + isNew;
        response.sendRedirect(frontendRedirect);
    }



    @Operation(summary = "이메일 인증번호 발송", description = "이메일 인증번호를 발송하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/send-email-verification")
    public BaseResponse<String> sendVerificationCode(@RequestParam String email) {
        try {
            String authCode = mailService.sendSimpleMessage(email);
            return new BaseResponse<>(HttpStatus.OK.value(), "메일 발송 성공", authCode, true);
        } catch (MessagingException e) {
            log.error("메일 발송 오류: {}", e.getMessage(), e);
            return new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "메일 발송 실패", "메일 발송 실패", false);
        }
    }

    @Operation(summary = "이메일 인증번호 확인", description = "사용자가 입력한 인증번호를 검증하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("check-verification-code")
    public BaseResponse<String> verifyEmailCode(@RequestParam String email, @RequestParam String code) {
        if (mailService.verifyCode(email, code)) {
            return new BaseResponse<>(HttpStatus.OK.value(), "인증에 성공하였습니다.", "인증에 성공하였습니다.", true);
        } else {
            return new BaseResponse<>(HttpStatus.BAD_REQUEST.value(), "인증 실패 (만료되었거나 잘못된 코드입니다)", "인증 실패", false);
        }
    }

    @Operation(summary = "비밀번호 검증", description = "비밀번호를 검증하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/validate")
    public BaseResponse<Boolean> validatePassword(@RequestBody PasswordRequest request) {
        boolean isValid = authService.isValidPassword(request.getPassword());
        return new BaseResponse<>(200, "비밀번호 검증 완료", isValid);

    }

    @Operation(summary = "닉네임 유효성 검사", description = "닉네임 유효성 검사 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/validate-nickname")
    public BaseResponse<Boolean> validateNickname(@RequestParam String nickname) {
        boolean isValid = authService.isValidNickname(nickname);
        return new BaseResponse<>(200, "닉네임 유효성 검사 완료", isValid);
    }

    @Operation(summary = "닉네임 중복 검사", description = "닉네임 중복 여부를 확인하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/check-nickname")
    public BaseResponse<Boolean> checkNicknameDuplicate(@Valid @RequestBody NicknameCheckRequest request) {
        try {
            // request 전체를 전달하도록 수정
            boolean isDuplicate = authService.isDuplicateNickname(request);
            return new BaseResponse<>(200, "닉네임 중복 검사 완료", isDuplicate);
        } catch (CustomException ce) {
            // CustomException의 메시지를 로그에 남깁니다.
            logger.error("CustomException: {}", ce.getMessage());
            throw ce;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BadRequest, "잘못된 요청입니다. 입력값을 확인해주세요.", HttpStatus.BAD_REQUEST);
        }
    }
}

