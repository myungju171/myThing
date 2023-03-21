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
import com.project.mything.user.dto.UserDto;
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

    @Transactional(readOnly = true)
    public ResponseEntity<String> search(String query, Integer size, String sort, Integer start) {
        return naverApiService.searchItem(query, size, sort, start);
    }

    public ItemDto.ResponseItemId saveItem(UserDto.UserInfo userInfo, ItemDto.RequestSaveItem requestSaveItem) {
        checkDuplicateItem(userInfo.getUserId(), requestSaveItem.getProductId());
        User dbUser = userService.findVerifiedUser(userInfo.getUserId());
        Item dbItem = saveItemFromDto(requestSaveItem);
        saveItemUser(dbUser, dbItem);
        return itemMapper.toResponseItemId(dbItem.getId());
    }

    private void saveItemUser(User dbUser, Item dbItem) {
        ItemUser itemUser = ItemUser.builder()
                .user(dbUser)
                .item(dbItem)
                .build()
                .addItemUser();
        itemUserRepository.save(itemUser);
    }

    @NotNull
    private Item saveItemFromDto(ItemDto.RequestSaveItem requestSaveItem) {
        Item item = itemRepository.findItemByProductId(requestSaveItem.getProductId())
                .orElse(itemMapper.toItem(requestSaveItem));
        if (item.getId() != null) return item;
        return itemRepository.save(item);
    }

    private void checkDuplicateItem(Long userId, Long productId) {
        if (itemUserRepository.findByUserIdAndProductId(userId, productId).isPresent())
            throw new BusinessLogicException(ErrorCode.ITEM_EXISTS);
    }

    @Transactional(readOnly = true)
    public ItemDto.ResponseDetailItem getDetailItem(UserDto.UserInfo userInfo, Long itemId) {
        ItemUser dbItemUser = verifyItemUser(userInfo.getUserId(), itemId);
        return itemMapper.toResponseDetailItem(dbItemUser, dbItemUser.getItem());
    }

    @Transactional(readOnly = true)
    public ResponseMultiPageDto<ItemDto.ResponseSimpleItem> getSimpleItems(Long userId, Boolean isFriend, Integer start, Integer size) {
        Page<ItemDto.ResponseSimpleItem> responseSimpleItems =
                itemUserRepository.searchSimpleItem(userId, isFriend, PageRequest.of(start - 1, size));
        List<ItemDto.ResponseSimpleItem> content = responseSimpleItems.getContent();

        return new ResponseMultiPageDto<ItemDto.ResponseSimpleItem>(content, responseSimpleItems);
    }

    public ItemDto.ResponseItemId changeItemStatus(UserDto.UserInfo userInfo, ItemDto.RequestChangeItemStatus requestChangeItemStatus) {
        validateSelfReserve(userInfo.getUserId(), requestChangeItemStatus.getReservedId());
        User reservedUser
                = verifyReservedUserId(requestChangeItemStatus.getReservedId());
        ItemUser dbItemUser
                = verifyItemUser(userInfo.getUserId(), requestChangeItemStatus.getItemId());
        validateExistReservedUser(dbItemUser);
        dbItemUser.updateItemStatus(requestChangeItemStatus.getItemStatus(), reservedUser);
        return itemMapper.toResponseItemId(dbItemUser.getItem().getId());
    }

    private void validateExistReservedUser(ItemUser dbItemUser) {
        if (dbItemUser.getReservedUser() != null) throw new BusinessLogicException(ErrorCode.ITEM_ALREADY_RESERVED);
    }

    private void validateSelfReserve(Long userId, Long reservedId) {
        if (userId.equals(reservedId))
            throw new BusinessLogicException(ErrorCode.RESERVE_USER_CONFLICT);
    }

    private ItemUser verifyItemUser(Long userId, Long itemId) {
        return itemUserRepository.findByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));
    }

    private User verifyReservedUserId(Long reservedUserId) {
        if (reservedUserId == null) return null;
        return userService.findVerifiedUser(reservedUserId);
    }

    public void cancelReservedItem(UserDto.UserInfo userInfo, ItemDto.RequestCancelReserveItem requestCancelReserveItem) {
        ItemUser dbItemUser = verifyItemUser(userInfo.getUserId(), requestCancelReserveItem.getItemId());
        Long reservedUserId = requestCancelReserveItem.getReservedUserId();
        verifyReservedUserId(reservedUserId);
        verifyItemStatus(dbItemUser.getItemStatus(), ItemStatus.RESERVE);
        verifyItemReservedUser(dbItemUser, reservedUserId);
        dbItemUser.cancelReserveItem();
    }

    private void verifyItemStatus(ItemStatus originItemStatus, ItemStatus itemStatus) {
        if (!originItemStatus.equals(itemStatus)) {
            throw new BusinessLogicException(ErrorCode.ITEM_STATUS_CONFLICT);
        }
    }

    private void verifyItemReservedUser(ItemUser dbItemUser, Long reserveUserId) {
        if (!dbItemUser.getReservedUser().getId().equals(reserveUserId)) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_MATCH);

        }
    }

    public void deleteItemUser(UserDto.UserInfo userInfo, Long itemId) {
        ItemUser dbItemUser = verifyItemUser(userInfo.getUserId(), itemId);
        verifyItemStatus(dbItemUser.getItemStatus(), ItemStatus.POST);
        deleteItemUser(dbItemUser);
    }

    private void deleteItemUser(ItemUser dbItemUser) {
        dbItemUser.getUser().getItemUserList().remove(dbItemUser);
        dbItemUser.getItem().getItemUserList().remove(dbItemUser);
        itemUserRepository.delete(dbItemUser);
    }

    public ItemDto.ResponseItemId changeItemInterest(UserDto.UserInfo userInfo, Long itemId) {
        ItemUser dbItemUser = verifyItemUser(userInfo.getUserId(), itemId);
        dbItemUser.changeItemInterest();
        return itemMapper.toResponseItemId(dbItemUser.getId());
    }

    public ItemDto.ResponseItemId changeItemSecret(UserDto.UserInfo userInfo, Long itemId) {
        ItemUser dbItemUser = verifyItemUser(userInfo.getUserId(), itemId);
        dbItemUser.changeItemSecret();
        return itemMapper.toResponseItemId(dbItemUser.getId());
    }
}
