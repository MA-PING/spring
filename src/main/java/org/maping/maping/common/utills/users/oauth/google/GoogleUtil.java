package org.maping.maping.common.utills.users.oauth.google;

import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maping.maping.common.utills.users.oauth.google.dto.GoogleTokenResponse;
import org.maping.maping.common.utills.users.oauth.google.dto.GoogleUserInfoResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class GoogleUtil {

    @Value("${spring.google.client-id}")
    private String clientId;

    @Value("${spring.google.client-secret}")
    private String clientSecret;

    @Value("${spring.google.redirect-uri}")
    private String redirectUri;

    @Value("${spring.google.provider.token-uri}")
    private String TOKEN_URL;

    @Value("${spring.google.provider.user-info-uri}")
    private String USER_INFO_URL;


    public String getAccessToken(String code) {
        GoogleTokenResponse googleTokenResponse = WebClient.create(TOKEN_URL)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("code", code)
                        .queryParam("redirect_uri", redirectUri)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(GoogleTokenResponse.class)
                .block();

        log.info("[Google Service] Access Token: {}", googleTokenResponse != null ? googleTokenResponse.getAccessToken() : null);
        log.info("[Google Service] Refresh Token: {}", googleTokenResponse != null ? googleTokenResponse.getRefreshToken() : null);
        log.info("[Google Service] Token Type: {}", googleTokenResponse != null ? googleTokenResponse.getTokenType() : null);

        return googleTokenResponse != null ? googleTokenResponse.getAccessToken() : null;
    }

    public GoogleUserInfoResponse getUserInfo(String accessToken) {
        GoogleUserInfoResponse userInfo = WebClient.create(USER_INFO_URL)
                .get()
                .uri(uriBuilder -> uriBuilder.scheme("https").build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(GoogleUserInfoResponse.class)
                .block();

        log.info("[Google Service] Name: {}", userInfo.getName());
        log.info("[Google Service] Email: {}", userInfo.getEmail());

        return userInfo;
    }

}
