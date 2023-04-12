package com.project.mything.item.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.item.config.ItemConfig;
import com.project.mything.item.dto.ItemDto;
import com.project.mything.item.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class NAVERApiServiceImpl implements NAVERApiService {

    private final ItemConfig itemConfig;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto.ResponseSearchItem searchItem(String query, Integer size, String sort, Integer start) {
        RestTemplate rest = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Naver-Client-Id", itemConfig.getPublicKey());
        httpHeaders.add("X-Naver-Client-Secret", itemConfig.getSecretKey());
        String body = "";
        HttpEntity<String> requestEntity = new HttpEntity<String>(body, httpHeaders);

        String url = "https://openapi.naver.com/v1/search/shop.json?query="
                + query + "&display=" + size + "&sort=" + sort + "&start=" + start;

        ResponseEntity<String> exchange = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);

        return toResponseSearchItem(exchange.getBody());
    }

    private ItemDto.ResponseSearchItem toResponseSearchItem(String body) {
        JSONObject jsonObject = new JSONObject(body);
        try {
            List<ItemDto.SearchItem> searchItemList = itemMapper.toSearchItemList(jsonObject.getJSONArray("items"));
            Integer start = jsonObject.getInt("start");
            Integer size = jsonObject.getInt("display");
            return itemMapper.toResponseSearchItem(searchItemList, size, start);
        } catch (JSONException e) {
            log.error("JsonException={}", e.getMessage());
            throw new BusinessLogicException(ErrorCode.NAVER_JSON_ERROR);
        }
    }
}
