package com.project.mything.auth.controller;

import com.project.mything.auth.dto.AuthDto;
import com.project.mything.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final AuthService authService;

    @PostMapping("/number")
    @ResponseStatus(HttpStatus.CREATED)
    public Boolean requestAuthNumber(@Valid @RequestBody AuthDto.RequestAuthNumber authNumber) {
        return authService.sendAuthNumber(authNumber);
    }

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthDto.ResponseLogin requestJoin(@Valid @RequestBody AuthDto.RequestJoin join) {
        return authService.join(join);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthDto.ResponseLogin login(@Valid @RequestBody AuthDto.RequestLogin requestLogin) {
        return authService.login(requestLogin);
    }
}
