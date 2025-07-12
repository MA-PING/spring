package org.maping.maping.common.utills.redis;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class JwtRedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> values;

    public JwtRedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.values = redisTemplate.opsForValue(); // String 타입을 쉽게 처리하는 메서드
    }

    /**
     * Refresh Token 저장
     * @param accessToken 사용자 ID
//     * @param tokenId 토큰 ID
     * @param refreshToken Refresh Token 문자열
     * @param duration 만료 시간 (초)
     */
    public void saveRefreshToken(String accessToken, String refreshToken, Duration duration) {
        String key = "refresh_token: accessToken: " + accessToken;
        values.set(key, refreshToken, duration);
    }

    /**
     * Refresh Token 조회
     * @param accessToken 사용자 ID
//     * @param tokenId 토큰 ID
     * @return Refresh Token 문자열 (없으면 null)
     */
    public String getRefreshToken(String accessToken) {
        String key = "refresh_token: accessToken: " + accessToken;
        return (String) values.get(key);
    }

    /**
     * Refresh Token 삭제 (무효화)
     * @param accessToken 사용자 ID
//     * @param tokenId 토큰 ID
     */
    public void deleteRefreshToken(String accessToken) {
        String key = "refresh_token: accessToken: " + accessToken;
        redisTemplate.delete(key);
    }

    /**
     * Access Token 블랙리스트에 추가
     * @param jti JWT ID (또는 Access Token의 해시)
     * @param duration 남은 만료 시간 (초)
     */
    public void addAccessTokenToBlacklist(String jti, Duration duration) {
        String key = "blacklist:access_token:" + jti;
        values.set(key, "true", duration);
    }

    /**
     * Access Token이 블랙리스트에 있는지 확인
     * @param jti JWT ID (또는 Access Token의 해시)
     * @return 블랙리스트에 있으면 true, 없으면 false
     */
    public boolean isAccessTokenBlacklisted(String jti) {
        String key = "blacklist:access_token:" + jti;
        return redisTemplate.hasKey(key);
    }
}
