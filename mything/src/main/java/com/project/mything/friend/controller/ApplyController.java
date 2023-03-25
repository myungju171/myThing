package com.project.mything.friend.controller;

import com.project.mything.friend.dto.ApplyDto;
import com.project.mything.friend.entity.enums.ApplyStatus;
import com.project.mything.friend.service.ApplyService;
import com.project.mything.security.jwt.service.JwtParseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/friends/applies")
@RequiredArgsConstructor
@Validated
public class ApplyController {

    private final JwtParseToken jwtParseToken;
    private final ApplyService applyService;

    @PostMapping("/{received-id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplyDto.ResponseApplyId createApply(@RequestHeader("Authorization") String token,
                                                @Positive @PathVariable("received-id") Long receivedId) {
        return applyService.createApply(jwtParseToken.getUserInfo(token), receivedId);
    }

    @DeleteMapping("/rejects")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectApply(@RequestHeader("Authorization") String token,
                            @Positive @RequestParam Long applyId) {
        applyService.rejectApply(jwtParseToken.getUserInfo(token), applyId);
    }

    @DeleteMapping("/cancels")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelApply(@RequestHeader("Authorization") String token,
                            @Positive @RequestParam Long applyId) {
        applyService.cancelApply(jwtParseToken.getUserInfo(token), applyId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptApply(@RequestHeader("Authorization") String token,
                            @Positive @RequestParam Long applyId) {
        applyService.acceptApply(jwtParseToken.getUserInfo(token), applyId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ApplyDto.ResponseSimpleApply> getSendApply(@RequestHeader("Authorization") String token,
                                                           @RequestParam Boolean isReceiveApply,
                                                           @RequestParam ApplyStatus applyStatus) {
        return applyService.getApply(jwtParseToken.getUserInfo(token), isReceiveApply, applyStatus);
    }
}
