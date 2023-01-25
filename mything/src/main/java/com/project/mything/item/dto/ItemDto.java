package com.project.mything.item.dto;

import com.project.mything.item.entity.enums.ItemStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

public class ItemDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestSaveItem {
        @NotNull
        @Positive
        private Long userId;
        @NotNull
        @Positive
        private Long productId;
        @NotBlank
        private String title;
        @NotBlank
        private String link;
        @NotBlank
        private String image;
        @NotNull
        @Positive
        private Integer price;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseItemId {
        private Long itemId;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseDetailItem {
        private Long itemId;
        private String title;
        private Integer price;
        private String link;
        private String image;
        private String memo;
        private Boolean interestedItem;
        private Boolean secretItem;
        private ItemStatus itemStatus;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseSimpleItem {

        private Long itemId;
        private String title;
        private Integer price;
        private String image;
        private Boolean interestedItem;
        private Boolean secretItem;
        private ItemStatus itemStatus;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;

        @QueryProjection
        public ResponseSimpleItem(Long itemId,
                                  String title,
                                  Integer price,
                                  String image,
                                  Boolean interestedItem,
                                  Boolean secretItem,
                                  ItemStatus itemStatus,
                                  LocalDateTime createdAt,
                                  LocalDateTime lastModifiedAt) {
            this.itemId = itemId;
            this.title = title;
            this.price = price;
            this.image = image;
            this.interestedItem = interestedItem;
            this.secretItem = secretItem;
            this.itemStatus = itemStatus;
            this.createdAt = createdAt;
            this.lastModifiedAt = lastModifiedAt;
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestChangeItemStatus {
        @NotNull
        @Positive
        private Long userId;
        @NotNull
        @Positive
        private Long itemId;
        @NotNull
        private ItemStatus itemStatus;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestCancelReserveItem {
        @NotNull
        @Positive
        private Long userId;
        @NotNull
        @Positive
        private Long itemId;
        @NotNull
        @Positive
        private Long reservedId;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestSimpleItem {
        @NotNull
        @Positive
        private Long userId;
        @NotNull
        @Positive
        private Long itemId;
    }
}
