package com.project.mything.security.jwt.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.project.mything.security.jwt.service.CustomDetailsService;
import com.project.mything.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@RequiredArgsConstructor
@Component
public class JwtTokenProvider  {
    @Value("${jwt.secretKey}")
    private String secretKey;

    private final CustomDetailsService userDetailsService;


    public String createToken(User user) {
        long tokenValidTime = 30 * 60 * 1000L;
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
        return JWT.decode(initToken(token)).getClaims().get("email").asString();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public boolean validateToken(String jwtToken) {
        try {
            Date expiresAt = JWT.decode(initToken(jwtToken)).getExpiresAt();
            return expiresAt.after(new Date());
        } catch (JWTDecodeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @NotNull
    private  String initToken(String jwtToken) {
        validationAuthorizationHeader(jwtToken);
        return extractToken(jwtToken);
    }

    private void validationAuthorizationHeader(String jwtToken) {
        if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException();
        }
    }

    private String extractToken(String jwtToken) {
        return jwtToken.replace("Bearer ", "");
    }

}
