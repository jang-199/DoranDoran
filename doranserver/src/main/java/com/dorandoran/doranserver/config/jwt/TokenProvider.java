package com.dorandoran.doranserver.config.jwt;

import com.dorandoran.doranserver.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Period;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final JwtProperties jwtProperties;



    public String generateAccessToken(Member user, Duration expireAt) {
        return makeToken(new Date(new Date().getTime() + expireAt.toMillis()), user);
    }

    /**
     *
     * @param user
     * @param expireAt 주기를 받아 밀리초로 변환하여 Date객체 생성 후 밀리초로 변환
     * @return
     */
    public String generateRefreshToken(Member user, Period expireAt) {
        return makeToken(new Date(new Date().getTime() + expireAt.toTotalMonths()*Duration.ofDays(30).toMillis()), user);
    }

    private String makeToken(Date expiry, Member user) {

        return Jwts.builder()
                .setHeaderParam(Header.TYPE,Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(expiry)
                .setSubject(user.getNickname())
                .claim("ROLE","ROLE_USER")
                .claim("email",user.getEmail())
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)),SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validToken(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(jwtToken);//복호화
            return true;
        } catch (Exception e) {
            log.info(e.getMessage());
            return false;
        }
    }

    private Object detailedValidateToken(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(jwtToken);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("JWT token has expired.");//유효기간 만료
            return e;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token: " + e.getMessage());//지원하지 않는 형식 또는 구조
            return e;
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token: " + e.getMessage()); //지원하지 않는 형식 또는 손상
            return e;
        } catch (SignatureException e) {
            log.info("Invalid JWT signature: " + e.getMessage()); //유효하지 않은 서명
            return e;
        } catch (JwtException e) {
            log.info("Invalid JWT token: " + e.getMessage()); //예상하지 못한 유효성 검사 실패
            return e;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        switch (claims.get("ROLE").toString()) {
            case "ROLE_USER" -> {
                log.info("user입니다");
                return getUserAuthentication(token);
            }
            case "ROLE_ADMIN" -> {
                return getAdminAuthentication(token);
            }
            default -> {
                throw new RuntimeException("토큰의 role을 확인할 수 없습니다.");
            }
        }
    }

    public Authentication getUserAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(new User(claims.get("email",String.class), "", authorities), token, authorities);
    }

    public Authentication getAdminAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return new UsernamePasswordAuthenticationToken(new User(claims.getSubject(), "", authorities), token, authorities);
    }

    public String getUserEmail(String token) {
        Claims claims = getClaims(token);
        return claims.get("email", String.class);
    }

    public Duration getExpiryDuration(String token) {
        Claims claims = getClaims(token);
        Date expiration = claims.getExpiration();
        return Duration.ofDays(expiration.getTime() - System.currentTimeMillis());
    }

    /**
     * 토큰 복호화 시 ExpiredJwtException 을 반환하면 true 를 반환함
     * @param token
     * @return
     */
    public Boolean isExpired(String token) {
        Object detailedValidateToken = detailedValidateToken(token);

        if (detailedValidateToken instanceof ExpiredJwtException) {
            return true;
        }
        return false;
    }
    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
