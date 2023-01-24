package com.project.mything.item.controller;

import com.project.mything.item.dto.ItemDto;
import com.project.mything.page.ResponseMultiPageDto;
import com.project.mything.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<String> search(@RequestParam String query,
                                         @RequestParam(required = false, defaultValue = "10") Integer size,
                                         @RequestParam(required = false, defaultValue = "sim") String sort,
                                         @RequestParam(required = false, defaultValue = "1") Integer start) {
        ResponseEntity<String> search = itemService.search(query, size, sort, start);
        return search;
    }

    @PostMapping("/storages")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto.ResponseItemId saveItem(@Valid @RequestBody ItemDto.RequestSaveItem requestSaveItem) {
        return itemService.saveItem(requestSaveItem);
    }

    @DeleteMapping("/storages")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@Valid @RequestBody ItemDto.RequestSimpleItem requestSimpleItem) {
        itemService.deleteItemUser(requestSimpleItem);
    }

    @GetMapping("/{item-id}/users/{user-id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto.ResponseDetailItem getDetailPage(@PathVariable("item-id") Long itemId,
                                                    @PathVariable("user-id") Long userId) {
        return itemService.getDetailItem(userId, itemId);
    }

    @GetMapping("/users/{user-id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseMultiPageDto<ItemDto.ResponseSimpleItem> getSimpleItems(@PathVariable("user-id") Long userId,
                                                                           @RequestParam Integer start,
                                                                           @RequestParam Integer size) {
        return itemService.getSimpleItems(userId, start, size);
    }

    @PatchMapping("/statuses")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto.ResponseItemId changeItemStatus(@Valid @RequestBody ItemDto.RequestChangeItemStatus requestChangeItemStatus,
                                                   @RequestHeader(required = false) Long reservedId) {
        return itemService.changeItemStatus(requestChangeItemStatus, reservedId);
    }

    @DeleteMapping("/statuses")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReserve(@Valid @RequestBody ItemDto.RequestCancelReserveItem requestCancelReserveItem) {
        itemService.cancelReservedItem(requestCancelReserveItem);
    }

    @PatchMapping("/interests")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto.ResponseItemId changeItemInterest (@Valid @RequestBody ItemDto.RequestSimpleItem requestSimpleItem ) {
        return itemService.changeItemInterest(requestSimpleItem);
    }

    @PatchMapping("/secrets")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto.ResponseItemId changeItemSecret (@Valid @RequestBody ItemDto.RequestSimpleItem requestSimpleItem ) {
        return itemService.changeItemSecret(requestSimpleItem);
    }
}
