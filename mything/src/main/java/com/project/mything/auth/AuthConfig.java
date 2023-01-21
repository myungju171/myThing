package com.project.mything.auth;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AuthConfig {
    @Value("${nurigo.public}")
    private final String publicKey;

    @Value("${nurigo.secret}")
    private final String secret;

    @Value("${nurigo.number}")
    private final String fromNumber;

    public AuthConfig(@Value("${nurigo.public}")String publicKey,
                      @Value("${nurigo.secret}")String secret,
                      @Value("${nurigo.number}")String fromNumber) {
        this.publicKey = publicKey;
        this.secret = secret;
        this.fromNumber = fromNumber;
    }
}
