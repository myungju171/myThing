package com.project.mything.item.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ItemConfig {
    @Value("${naver.X-Naver-Client-Id}")
    private final String publicKey;
    @Value("${naver.X-Naver-Client-Secret}")
    private final String secretKey;

    public ItemConfig(@Value("${naver.X-Naver-Client-Id}") String publicKey, @Value("${naver.X-Naver-Client-Secret}") String secret) {
        this.publicKey = publicKey;
        this.secretKey = secret;
    }
}
