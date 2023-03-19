package com.project.mything.user.dto;

import lombok.*;

public class ImageDto {
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SimpleImageDto {
        private Long imageId;
        private String remotePath;
    }
}
