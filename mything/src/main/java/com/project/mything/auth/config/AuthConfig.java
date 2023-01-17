package com.project.mything.auth.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AuthConfig {
    @Value("${nurigo.public}")
    private String publicKey;

    @Value("${nurigo.secret}")
    private String secretKey;

    @Value("${nurigo.number}")
    private String fromNumber;
}
