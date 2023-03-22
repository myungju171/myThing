package com.project.mything.item.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ItemConfig {
    @Value("${naver.X-Naver-Client-Id}")
    private String publicKey;
    @Value("${naver.X-Naver-Client-Secret}")
    private String secretKey;

}
