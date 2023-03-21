package com.project.mything.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

public class ImageDto {
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SimpleImageDto {
        private Long imageId;
        private String remotePath;

        @QueryProjection
        public SimpleImageDto(Long imageId, String remotePath) {
            this.imageId = imageId;
            this.remotePath = remotePath;
        }
    }
}
