package org.maping.maping.common.utills.users.oauth.naver;

import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maping.maping.external.oauth.naver.dto.response.NaverTokenResponse;
import org.maping.maping.external.oauth.naver.dto.response.NaverUserInfoResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
@Slf4j
@RequiredArgsConstructor
public class NaverUtil {
    @Value("${spring.naver.client-id}")
    private String clientId;
    @Value("${spring.naver.client-secret}")
    private String clientSecret;
    @Value("${spring.naver.redirect-uri}")
    private String redirectUri;
    @Value("${spring.naver.provider.token-uri}")
    private String TOKEN_URL;
    @Value("${spring.naver.provider.user-info-uri}")
    private String USER_INFO_URL;

    public String getAccessToken(String code, String state) {
        if (state == null || state.isEmpty()) {
            throw new IllegalArgumentException("State 값이 누락되었습니다.");
        }

        NaverTokenResponse naverTokenResponse = WebClient.create(TOKEN_URL).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("code", code)
                        .queryParam("state", state)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(NaverTokenResponse.class)
                .block();


        log.info(" [Naver Service] Access Token ------> {}", naverTokenResponse != null ? naverTokenResponse.getAccessToken() : null);
        log.info(" [Naver Service] Refresh Token ------> {}", naverTokenResponse != null ? naverTokenResponse.getRefreshToken() : null);
        log.info(" [Naver Service] Token Type ------> {}", naverTokenResponse != null ? naverTokenResponse.getTokenType() : null);

        return naverTokenResponse != null ? naverTokenResponse.getAccessToken() : null;
    }

    public NaverUserInfoResponse getUserInfo(String accessToken) {
        NaverUserInfoResponse userInfo = WebClient.create(USER_INFO_URL)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(NaverUserInfoResponse.class)
                .block();

        log.info("[ Naver Service ] Name ---> {} ", userInfo.getResponse().getName());
        log.info("[ Naver Service ] Email ---> {} ", userInfo.getResponse().getEmail());

        return userInfo;
    }

    }
