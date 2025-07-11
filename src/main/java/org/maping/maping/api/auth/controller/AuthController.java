package org.maping.maping.api.auth.controller;

import groovy.util.logging.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.maping.maping.api.auth.dto.request.*;
import org.maping.maping.api.auth.dto.response.OAuthLoginResponse;
import org.maping.maping.api.auth.service.AuthService;
import org.maping.maping.api.auth.service.MailService;
import org.maping.maping.api.auth.service.OAuthService;
import org.maping.maping.common.enums.expection.ErrorCode;
import org.maping.maping.common.utills.jwt.dto.JwtDto;
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



        Cookie accessTokenCookie = new Cookie("accessToken", accessToken); // 쿠키 이름은 'accessToken', 값은 발급받은 JWT


        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);

        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return new BaseResponse<>(HttpStatus.OK.value(), "로그인 성공", jwtDto, true);
    }

    @Operation(summary = "토큰 재발급", description = "리프레쉬 토큰 기반 액세스 토큰을 재발급 받는 API")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/reissue")
    public BaseResponse<JwtDto> reissue(@RequestBody ReissueRequest request) {
        JwtDto newTokens = authService.reissue(request.getRefreshToken());


        return new BaseResponse<>(HttpStatus.OK.value(), "재발급 성공", newTokens, true);
    }


    @Operation(summary = "네이버 로그인", description = "네이버 로그인 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/signup/naver")
    public BaseResponse<OAuthLoginResponse> naverLogin(
            @Parameter(description = "네이버 요청을 통해 받아온 엑세스 토큰")
            @RequestParam("code") String code,
            @Parameter(description = "네이버 요청 시 생성된 상태값")
            @RequestParam("state") String state
    ) {
        OAuthLoginResponse response = oAuthService.naverLogin(code, state);
        return new BaseResponse<>(HttpStatus.OK.value(), "네이버 로그인 성공", response, true);
    }

    @Operation(summary = "구글 로그인", description = "구글 로그인 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/signup/google")
    public BaseResponse<OAuthLoginResponse> googleLogin(
            @Parameter(description = "구글 요청을 통해 받아온 엑세스 토큰")
            @RequestParam("code") String code
    ) {
        OAuthLoginResponse response = oAuthService.googleLogin(code);
        return new BaseResponse<>(HttpStatus.OK.value(), "구글 로그인 성공", response, true);
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

