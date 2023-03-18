package com.project.mything.security.auth.controller;

import com.project.mything.security.auth.service.PhoneAuthService;
import com.project.mything.security.auth.dto.PhoneAuthDto;
import com.project.mything.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class PhoneAuthController {
    private final PhoneAuthService authService;

    @PostMapping("/message")
    @ResponseStatus(HttpStatus.CREATED)
    public String requestAuthNumber(@Valid @RequestBody PhoneAuthDto.RequestAuthNumber authNumber) {
        return authService.sendAuthNumber(authNumber);
    }

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto.ResponseJoinUser requestJoin(@Valid @RequestBody PhoneAuthDto.RequestJoin join) {
        return authService.join(join);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto.ResponseJoinUser login(@Valid @RequestBody PhoneAuthDto.RequestLogin requestLogin) {
        return authService.login(requestLogin);
    }
}
