package com.project.mything.security.auth.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AuthConfig {
    @Value("${nurigo.public}")
    private String publicKey;

    @Value("${nurigo.secret}")
    private String secret;

    @Value("${nurigo.number}")
    private String fromNumber;

}
