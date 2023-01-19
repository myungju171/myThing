package com.project.mything.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ItemDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestSaveItem {
        @NotNull
        private Long userId;
        @NotNull
        private Long productId;
        @NotBlank
        private String title;
        @NotBlank
        private String link;
        @NotBlank
        private String image;
        @NotNull
        private Integer lprice;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseItemId {
        private Long itemId;
    }
}
