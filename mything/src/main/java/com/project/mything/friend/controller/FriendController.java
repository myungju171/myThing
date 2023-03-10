package com.project.mything.friend.controller;

import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
@Validated
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public FriendDto.ResponseSimpleFriend searchFriend(@NotBlank @Size(min = 11, max = 11) @RequestParam String friendPhone) {
        return friendService.searchFriend(friendPhone);
    }

    @GetMapping("/charts")
    @ResponseStatus(HttpStatus.OK)
    public FriendDto.ResponseMultiFriend<FriendDto.ResponseSimpleFriend> getFriends(
           @Valid @RequestBody FriendDto.RequestFriendList requestFriendList) {
        return friendService.getFriendInfo(requestFriendList);
    }

}
