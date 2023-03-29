package com.project.mything.user.controller;

import com.project.mything.security.jwt.service.JwtParseToken;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final JwtParseToken jwtParseToken;

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto.ResponseDetailUser editProfile(@RequestHeader("Authorization") String token,
                                                  @Valid @RequestBody UserDto.RequestEditProFile requestEditProFile) {
        return userService.editProFile(jwtParseToken.getUserInfo(token), requestEditProFile);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto.ResponseDetailUser getUserDetail(@RequestHeader("Authorization") String token) {
        return userService.getUserDetail(jwtParseToken.getUserInfo(token));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdrawal(@RequestHeader("Authorization") String token) {
        userService.withdrawal(jwtParseToken.getUserInfo(token));
    }

    @PatchMapping("/passwords")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestHeader("Authorization") String token,
                               @Valid @RequestBody UserDto.RequestChangePassword requestChangePassword) {
        userService.changePassword(jwtParseToken.getUserInfo(token), requestChangePassword);
    }
}
