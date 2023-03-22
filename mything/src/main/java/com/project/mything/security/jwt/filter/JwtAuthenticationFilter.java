package com.project.mything.security.jwt.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.project.mything.security.jwt.exception.ExpiredTokenException;
import com.project.mything.security.jwt.service.JwtTokenProvider;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = jwtTokenProvider.resolveToken(request);
        try {
            if (bearerToken != null) {
                String token = jwtTokenProvider.initToken(bearerToken);
                jwtTokenProvider.validateToken(token);
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredTokenException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            setResponse(response, "EXPIRED_TOKEN_");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            setResponse(response, "MALFORMED_TOKEN_");
        } catch (JWTDecodeException e) {
            log.error("JWT token is invalid: {}", e.getMessage());
            setResponse(response, "INVALID_TOKEN_");
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            setResponse(response, "UNSUPPORTED_TOKEN_");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            setResponse(response, "INVALID_TOKEN_");
        }
        filterChain.doFilter(request, response);
    }

    public void setResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(message);
    }

}
