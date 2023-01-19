package com.project.mything.item.controller;

import com.project.mything.item.dto.ItemDto;
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
                                         @RequestParam(required = false, defaultValue = "sim") String sort) {
        ResponseEntity<String> search = itemService.search(query, size, sort);
        return new ResponseEntity<String>(search.getBody(), search.getStatusCode());
    }

    @PostMapping("/storages")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto.ResponseItemId saveItem(@Valid @RequestBody ItemDto.RequestSaveItem requestSaveItem) {
        return itemService.saveItem(requestSaveItem);
    }
}
