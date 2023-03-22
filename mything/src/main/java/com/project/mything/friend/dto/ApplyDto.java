package com.project.mything.friend.dto;

import com.project.mything.user.dto.UserDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

public class ApplyDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ResponseApplyId {
        private Long applyId;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseSimpleApply {
        private Long applyId;
        private UserDto.ResponseSimpleUser user;

        @QueryProjection
        public ResponseSimpleApply(Long applyId, UserDto.ResponseSimpleUser user) {
            this.applyId = applyId;
            this.user = user;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RequestSimpleApply {
        private Long receiveUserId;
    }
}
