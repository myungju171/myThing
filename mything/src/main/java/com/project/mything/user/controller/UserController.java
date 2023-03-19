package com.project.mything.user.controller;

import com.project.mything.security.jwt.service.JwtParseToken;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final JwtParseToken jwtParseToken;

    @PatchMapping("/profiles")
    @ResponseStatus(HttpStatus.OK)
    public UserDto.ResponseDetailUser editProfile(@RequestHeader("Authorization") String token,
                                                  @RequestBody UserDto.RequestEditProFile requestEditProFile) {
        return userService.editProFile(jwtParseToken.getUserInfo(token), requestEditProFile);
    }

    @GetMapping("/{user-id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto.ResponseDetailUser getUserInfo(@PathVariable("user-id") Long userId) {
        return userService.getUserInfo(userId);
    }
}
