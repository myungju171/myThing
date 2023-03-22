package com.project.mything.security.jwt.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.project.mything.security.jwt.exception.ExpiredTokenException;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements JwtParseToken{
    @Value("${jwt.secretKey}")
    private String secretKey;

    private final CustomDetailsService userDetailsService;


    public String createToken(User user) {
//        long tokenValidTime = 30 * 60 * 1000L;
        long tokenValidTime = 1000 * 60 * 60; //1시간
        return "Bearer " +
                JWT.create()
                        .withSubject("JWT")
                        .withExpiresAt(new Date(System.currentTimeMillis() + tokenValidTime))
                        .withClaim("id", user.getId())
                        .withClaim("email", user.getEmail())
                        .withClaim("name", user.getName())
                        .sign(Algorithm.HMAC256(secretKey));
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserEmail(String token) {
        return JWT.decode(token).getClaims().get("email").asString();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public void validateToken(String jwtToken) {
        Date expiresAt = JWT.decode(jwtToken).getExpiresAt();
        if (expiresAt.before(new Date()))
            throw new ExpiredTokenException("expired");
    }


    public String initToken(String jwtToken) {
        validationAuthorizationHeader(jwtToken);
        return extractToken(jwtToken);
    }

    private void validationAuthorizationHeader(String jwtToken) {
        if (!jwtToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException();
        }
    }

    private String extractToken(String jwtToken) {
        return jwtToken.replace("Bearer ", "");
    }

    @Override
    public UserDto.UserInfo getUserInfo(String token) {
        Map<String, Claim> claims = JWT.decode(extractToken(token)).getClaims();
        return UserDto.UserInfo.builder()
                .userId(claims.get("id").asLong())
                .email(claims.get("email").asString())
                .name(claims.get("name").asString())
                .build();
    }

}
