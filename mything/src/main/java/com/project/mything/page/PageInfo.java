package com.project.mything.page;

import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PageInfo implements Serializable {
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
}
