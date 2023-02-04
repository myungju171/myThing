package com.project.mything.user.controller;

import com.project.mything.user.dto.UserDto;
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

    @PostMapping("/profiles")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto.ResponseImageURl editProfile(@RequestParam(required = false) MultipartFile multipartFile,
                                                @RequestParam @NotNull @Positive Long userId,
                                                @RequestParam @NotBlank String name,
                                                @RequestParam(required = false) String infoMessage,
                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDay) {

        return userService.uploadImageAndEditUserProfile(multipartFile, userId, name, infoMessage, birthDay);
    }

    @DeleteMapping("/avatars")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvatar(@RequestBody UserDto.RequestUserId requestUserId) {
        userService.deleteAvatar(requestUserId.getUserId());
    }

    @GetMapping("/{user-id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto.ResponseDetailUser getUserInfo(@PathVariable("user-id") Long userId) {
        return userService.getUserInfo(userId);
    }

}
