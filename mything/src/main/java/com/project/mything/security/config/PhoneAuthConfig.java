package com.project.mything.security.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class PhoneAuthConfig {
    @Value("${nurigo.public}")
    private String publicKey;

    @Value("${nurigo.secret}")
    private String secret;

    @Value("${nurigo.number}")
    private String fromNumber;

}
