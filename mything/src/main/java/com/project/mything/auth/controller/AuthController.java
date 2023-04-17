package com.project.mything.auth.controller;

import com.project.mything.auth.dto.AuthDto;
import com.project.mything.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

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

    @GetMapping("/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void duplicateEmail(@Valid @Email @NotBlank @RequestParam String email) {
        authService.duplicateEmail(email);
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void findPassword(@Valid @RequestBody AuthDto.RequestFindPassword requestFindPassword) {
        authService.findPassword(requestFindPassword);
    }

}
