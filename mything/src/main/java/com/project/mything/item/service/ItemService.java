package com.project.mything.item.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.item.dto.ItemDto;
import com.project.mything.item.entity.Item;
import com.project.mything.item.entity.ItemUser;
import com.project.mything.item.mapper.ItemMapper;
import com.project.mything.item.repository.ItemRepository;
import com.project.mything.item.repository.ItemUserRepository;
import com.project.mything.user.entity.User;
import com.project.mything.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    @Value("${naver.X-Naver-Client-Id}")
    private String publicKey;
    @Value("${naver.X-Naver-Client-Secret}")
    private String secretKey;

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final ItemUserRepository itemUserRepository;
    private final UserService userService;

    public ResponseEntity<String> search(String query, Integer size, String sort) {
        if (query.equals("") || query == null) {
            throw new BusinessLogicException(ErrorCode.INCORRECT_QUERY_REQUEST);
        }
        RestTemplate rest = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Naver-Client-Id", publicKey);
        httpHeaders.add("X-Naver-Client-Secret", secretKey);
        String body = "";

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, httpHeaders);

        ResponseEntity<String> responseEntity = rest.exchange(
                "https://openapi.naver.com/v1/search/shop.json?query=" + query + "&display=" + size + "&sort=" + sort,
                HttpMethod.GET, requestEntity, String.class);
        return responseEntity;

    }

    public ItemDto.ResponseItemId saveItem(ItemDto.RequestSaveItem requestSaveItem) {
        duplicateItem(requestSaveItem);

        User dbUser =
                userService.findVerifiedUser(requestSaveItem.getUserId());
        Item dbItem =
                saveItemFromDto(requestSaveItem);

        itemUserRepository.save(ItemUser.builder().user(dbUser).item(dbItem).build().addItemUser());

        return itemMapper.toResponseItemId(dbItem.getId());
    }

    @NotNull
    private Item saveItemFromDto(ItemDto.RequestSaveItem requestSaveItem) {
        Item item = itemMapper.toItem(requestSaveItem);
        return itemRepository.save(item);
    }

    private void duplicateItem(ItemDto.RequestSaveItem requestSaveItem) {
        if (itemUserRepository.
                findItemUserByUserIdAndProductId(requestSaveItem.getUserId(), requestSaveItem.getProductId()).isPresent()) {
            throw new BusinessLogicException(ErrorCode.ITEM_EXISTS);
        }
    }
}
