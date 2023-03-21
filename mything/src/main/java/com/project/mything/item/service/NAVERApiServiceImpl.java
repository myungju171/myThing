package com.project.mything.item.service;

import com.project.mything.item.config.ItemConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NAVERApiServiceImpl implements NAVERApiService {

    private final ItemConfig itemConfig;

    @Override
    public ResponseEntity<String> searchItem(String query, Integer size, String sort, Integer start) {
        RestTemplate rest = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-Naver-Client-Id", itemConfig.getPublicKey());
        httpHeaders.add("X-Naver-Client-Secret", itemConfig.getSecretKey());
        String body = "";

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, httpHeaders);

        return rest.exchange(
                "https://openapi.naver.com/v1/search/shop.json?query="
                        + query + "&display=" + size + "&sort=" + sort + "&start=" + start,
                HttpMethod.GET, requestEntity, String.class);
    }
}
