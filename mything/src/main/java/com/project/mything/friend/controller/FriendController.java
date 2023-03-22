package com.project.mything.friend.controller;

import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.entity.enums.FriendStatus;
import com.project.mything.friend.service.FriendService;
import com.project.mything.page.ResponseMultiPageDto;
import com.project.mything.security.jwt.service.JwtParseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
@Validated
public class FriendController {

    private final FriendService friendService;
    private final JwtParseToken jwtParseToken;

    @GetMapping("/searches")
    @ResponseStatus(HttpStatus.OK)
    public FriendDto.ResponseSimpleFriend searchFriend(@NotBlank @Size(min = 11, max = 11) @RequestParam String friendPhone) {
        return friendService.searchFriend(friendPhone);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseMultiPageDto<FriendDto.ResponseSimpleFriend> getFriendsList(@RequestHeader("Authorization") String token,
                                                                               @RequestParam FriendStatus friendStatus,
                                                                               @RequestParam Boolean isBirthday) {
        return friendService.getFriendsList(jwtParseToken.getUserInfo(token), friendStatus, isBirthday);
    }
}
