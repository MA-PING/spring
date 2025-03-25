package org.maping.maping.common.utills.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.maping.maping.exceptions.CustomException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.maping.maping.common.enums.expection.ErrorCode;
import org.maping.maping.common.utills.jwt.dto.JwtDto;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JWTUtill {

    private final long ACCESS_TOKEN_EXPIRE_TIME;
    private final long REFRESH_TOKEN_EXPIRE_TIME;
    private final SecretKey key;

    public static String MDC_USER_ID = "userId";

    public JWTUtill(
            @Value("${spring.jwt.secret-key}") String secretKey,
            @Value("${spring.jwt.access-token-expired}") long ACCESS_TOKEN_EXPIRE_TIME,
            @Value("${spring.jwt.refresh-token-expired}") long REFRESH_TOKEN_EXPIRE_TIME
    ) {
        this.ACCESS_TOKEN_EXPIRE_TIME = ACCESS_TOKEN_EXPIRE_TIME;
        this.REFRESH_TOKEN_EXPIRE_TIME = REFRESH_TOKEN_EXPIRE_TIME;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public JwtDto generateJwtDto(String userId, String role) {
        Date now = new Date();
        Date accessTokenExpiresIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME * 1000);
        Date refreshTokenExpiresIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME * 1000);

        String issuer = "Maping";

        String accessToken = createToken(userId, role, accessTokenExpiresIn, "AT");
        String refreshToken = createToken(userId, role, refreshTokenExpiresIn, "RT");

        return JwtDto.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType("Bearer")
                .expiresIn(accessTokenExpiresIn.getTime() / 1000)
                .build();
    }

    private String createToken(String userId, String role, Date expirationDate, String type) {
        Date now = new Date();
        return Jwts.builder()
                .setIssuer("skuhub")
                .setSubject(userId)
                .setExpiration(expirationDate)
                .setIssuedAt(now)
                .signWith(key)
                .claim("userId", userId)
                .claim("type", type)
                .claim("role", role)
                .compact();
    }

    public boolean isValidAccessToken(String token) {
        return isValidToken(token, "AT");
    }

    public boolean isValidRefreshToken(String token) {
        return isValidToken(token, "RT");
    }

    private boolean isValidToken(String token, String expectedType) {
        try {
            Claims claims = getClaims(token);
            return claims.get("type").equals(expectedType);
        } catch (ExpiredJwtException e) {
            log.error("{} 토큰이 만료되었습니다. {}", expectedType, e.getMessage());
            return false;
        } catch (JwtException e) {
            log.error("{} 토큰이 유효하지 않습니다. {}", expectedType, e.getMessage());
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    public String putUserMDC(Claims claims) {
        String userId = (String) claims.get("userId");
        MDC.put(MDC_USER_ID, userId);
        return userId;
    }

    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String subString = header.substring(7);
            if (StringUtils.hasText(subString)) {
                return subString;
            }
        }
        throw new CustomException(ErrorCode.Unauthorized, "JWT 토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
    }

    public String getUserId(HttpServletRequest request) {
        String resolveToken = resolveToken(request);
        if(!isValidAccessToken(resolveToken)) {
            return putUserMDC(getClaims(resolveToken));
        }else{
            throw new CustomException(ErrorCode.Unauthorized, "JWT 토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }
    }
}
