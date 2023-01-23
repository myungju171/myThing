package com.project.mything.item.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.item.dto.ItemDto;
import com.project.mything.item.entity.enums.ItemStatus;
import com.project.mything.page.ResponseMultiPageDto;
import com.project.mything.item.entity.Item;
import com.project.mything.item.entity.ItemUser;
import com.project.mything.item.mapper.ItemMapper;
import com.project.mything.item.repository.ItemRepository;
import com.project.mything.item.repository.ItemUserRepository;
import com.project.mything.user.entity.User;
import com.project.mything.user.mapper.UserMapper;
import com.project.mything.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final ItemUserRepository itemUserRepository;
    private final UserService userService;
    private final NAVERApiService naverApiService;
    private final UserMapper userMapper;

    public ResponseEntity<String> search(String query, Integer size, String sort, Integer start) {
        if (query.equals("")) {
            throw new BusinessLogicException(ErrorCode.INCORRECT_QUERY_REQUEST);
        }
        return naverApiService.searchItem(query, size, sort, start);
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

    @Transactional(readOnly = true)
    public ItemDto.ResponseDetailItem getDetailItem(Long userId, Long itemId) {
        ItemUser dbItemUser = itemUserRepository.findItemUserByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));

        return itemMapper.toResponseDetailItem(dbItemUser, dbItemUser.getItem());
    }

    @Transactional(readOnly = true)
    public ResponseMultiPageDto<ItemDto.ResponseSimpleItem> getSimpleItems(Long userId, Integer start, Integer size) {
        User dbUser = userService.findVerifiedUser(userId);

        PageRequest pageRequest = PageRequest.of(start - 1, size);

        Page<ItemDto.ResponseSimpleItem> responseSimpleItems =
                itemUserRepository.searchSimpleItem(userId, pageRequest);

        List<ItemDto.ResponseSimpleItem> content =
                responseSimpleItems.getContent();

        return new ResponseMultiPageDto<ItemDto.ResponseSimpleItem>(
                content,
                responseSimpleItems,
                userMapper.toResponseSimpleUser(dbUser));
    }

    public ItemDto.ResponseItemId changeItemStatus(ItemDto.RequestChangeItemStatus requestChangeItemStatus,
                                                   Long reservedId) {
        ItemUser dbItemUser = duplicateUserIdAndItemId(requestChangeItemStatus.getUserId(),requestChangeItemStatus.getItemId(), reservedId);

        if (dbItemUser.getReservedUserId() != null) {
            throw new BusinessLogicException(ErrorCode.ITEM_ALREADY_RESERVED);
        }
        dbItemUser.changeItemStatus(requestChangeItemStatus.getItemStatus(), reservedId);

        return itemMapper.toResponseItemId(dbItemUser.getItem().getId());
    }

    private ItemUser duplicateUserIdAndItemId(Long userId, Long itemId, Long reservedId) {
        if (reservedId != null) {
            userService.findVerifiedUser(reservedId);
        }
        ItemUser dbItemUser = itemUserRepository
                .findItemUserByUserIdAndItemId(userId,itemId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));
        return dbItemUser;
    }


    public void cancelReservedItem(ItemDto.RequestCancelReserveItem requestCancelReserveItem) {
        ItemUser dbItemUser = duplicateUserIdAndItemId(
                requestCancelReserveItem.getUserId(),
                requestCancelReserveItem.getItemId(),
                requestCancelReserveItem.getReservedId());
        if (!dbItemUser.getItemStatus().equals(ItemStatus.RESERVE)) {
            throw new BusinessLogicException(ErrorCode.ITEM_STATUS_NOT_RESERVE);
        }
        if (!dbItemUser.getReservedUserId().equals(requestCancelReserveItem.getReservedId())) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_MATCH);
        }
        dbItemUser.cancelReserveItem();
    }

}
