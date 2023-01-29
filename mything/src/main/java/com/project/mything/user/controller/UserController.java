package com.project.mything.user.controller;

import com.project.mything.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PatchMapping("/profiles")
    @ResponseStatus(HttpStatus.OK)
    public String editProfile(@RequestParam(required = false) MultipartFile multipartFile,
                              @RequestParam @NotNull @Positive Long userId,
                              @RequestParam @NotBlank String name,
                              @RequestParam @NotBlank String infoMessage,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDay) {

        String remotePath = userService.uploadImage(multipartFile, userId);
        userService.editUserProfile(userId, name, infoMessage, birthDay);
        return remotePath;
    }

}
