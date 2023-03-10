package com.project.mything.auth.controller;

import com.project.mything.auth.service.PhoneAuthService;
import com.project.mything.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import static com.project.mything.auth.dto.PhoneAuthDto.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class PhoneAuthController {
    private final PhoneAuthService authService;

    @PostMapping("/message")
    @ResponseStatus(HttpStatus.CREATED)
    public String requestAuthNumber(@Valid @RequestBody RequestAuthNumber authNumber) {
        return authService.sendAuthNumber(authNumber);
    }

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto.ResponseUserId requestJoin(@Valid @RequestBody RequestJoin join) {
        return authService.join(join);
    }
}
