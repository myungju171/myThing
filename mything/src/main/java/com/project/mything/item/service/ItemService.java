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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final ItemUserRepository itemUserRepository;
    private final UserService userService;
    private final NAVERApiService naverApiService;

    public ResponseEntity<String> search(String query, Integer size, String sort) {
        if (query.equals("")) {
            throw new BusinessLogicException(ErrorCode.INCORRECT_QUERY_REQUEST);
        }
        return naverApiService.searchItem(query, size, sort);
    }

    public ItemDto.ResponseItemId saveItem(ItemDto.RequestSaveItem requestSaveItem) {
        duplicateItem(requestSaveItem);

        User dbUser =
                userService.findVerifiedUser(requestSaveItem.getUserId());
        Item dbItem =
                saveItemFromDto(requestSaveItem);

        ItemUser itemUser = ItemUser.builder()
                .user(dbUser)
                .item(dbItem)
                .build()
                .addItemUser();
        itemUserRepository.save(itemUser);

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

    public ItemDto.ResponseDetailItem getDetailItem(Long userId, Long itemId) {
        ItemUser dbItemUser = itemUserRepository.findItemUserByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));

        return itemMapper.toResponseDetailItem(dbItemUser, dbItemUser.getItem());
    }
}
