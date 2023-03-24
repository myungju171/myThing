package com.project.mything.image.controller;

import com.project.mything.image.dto.ImageDto;
import com.project.mything.image.service.ImageService;
import com.project.mything.security.jwt.service.JwtParseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping()
@RequiredArgsConstructor
@Validated
public class ImageController {

    private final ImageService imageService;
    private final JwtParseToken jwtParseToken;

    @PostMapping("/users/avatars")
    @ResponseStatus(HttpStatus.CREATED)
    public ImageDto.SimpleImageDto upload(@Valid @RequestParam MultipartFile multipartFile) {
        return imageService.uploadImage(multipartFile);
    }

    @DeleteMapping("users/avatars")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvatar(@RequestHeader("Authorization") String token) {
        imageService.deleteImage(jwtParseToken.getUserInfo(token).getUserId());
    }
}
