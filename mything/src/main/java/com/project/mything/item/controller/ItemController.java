package com.project.mything.item.controller;

import com.project.mything.item.dto.ItemDto;
import com.project.mything.page.ResponseMultiPageDto;
import com.project.mything.item.service.ItemService;
import com.project.mything.security.jwt.service.JwtParseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    private final JwtParseToken jwtParseToken;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemDto.ResponseSearchItem search(@Valid @NotBlank @RequestParam String query,
                                             @RequestParam(required = false, defaultValue = "10") Integer size,
                                             @RequestParam(required = false, defaultValue = "sim") String sort,
                                             @RequestParam(required = false, defaultValue = "1") Integer start) {
        return itemService.search(query, size, sort, start);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto.ResponseItemId saveItem(@RequestHeader("Authorization") String token,
                                           @Valid @RequestBody ItemDto.RequestSaveItem requestSaveItem) {
        return itemService.saveItem(jwtParseToken.getUserInfo(token), requestSaveItem);
    }

    @DeleteMapping("/{item-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@RequestHeader("Authorization") String token,
                           @PathVariable("item-id") Long itemId) {
        itemService.deleteItemUser(jwtParseToken.getUserInfo(token), itemId);
    }

    @GetMapping("/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto.ResponseDetailItem getDetailPage(@RequestHeader("Authorization") String token,
                                                    @PathVariable("item-id") Long itemId) {
        return itemService.getDetailItem(jwtParseToken.getUserInfo(token), itemId);
    }

    @GetMapping("/users/{user-id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseMultiPageDto<ItemDto.ResponseSimpleItem> getSimpleItemsMine(@Valid @PathVariable("user-id") Long userId,
                                                                               @Valid @RequestParam(required = false, defaultValue = "TRUE") Boolean isWish,
                                                                               @Valid @RequestParam(required = false, defaultValue = "TRUE") Boolean isFriend,
                                                                               @Valid @RequestParam(required = false, defaultValue = "null") String sortBy,
                                                                               @Valid @RequestParam Integer start,
                                                                               @Valid @RequestParam Integer size) {
        return itemService.getSimpleItems(userId, isWish, isFriend, sortBy, start, size);
    }

    @PatchMapping("/statuses")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto.ResponseItemId changeItemStatus(@RequestHeader("Authorization") String token,
                                                   @Valid @RequestBody ItemDto.RequestChangeItemStatus requestChangeItemStatus) {
        return itemService.changeItemStatus(jwtParseToken.getUserInfo(token), requestChangeItemStatus);
    }

    @DeleteMapping("/statuses")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReserve(@RequestHeader("Authorization") String token,
                              @Valid @RequestBody ItemDto.RequestCancelReserveItem requestCancelReserveItem) {
        itemService.cancelReservedItem(jwtParseToken.getUserInfo(token), requestCancelReserveItem);
    }

    @PatchMapping("/interests/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto.ResponseItemId changeItemInterest(@RequestHeader("Authorization") String token,
                                                     @PathVariable("item-id") Long itemId) {
        return itemService.changeItemInterest(jwtParseToken.getUserInfo(token), itemId);
    }

    @PatchMapping("/secrets/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto.ResponseItemId changeItemSecret(@RequestHeader("Authorization") String token,
                                                   @PathVariable("item-id") Long itemId) {
        return itemService.changeItemSecret(jwtParseToken.getUserInfo(token), itemId);
    }
}
