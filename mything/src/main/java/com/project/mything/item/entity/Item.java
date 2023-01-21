package com.project.mything.item.entity;

import com.project.mything.item.entity.enums.ItemStatus;
import com.project.mything.time.BaseTime;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private Long productId;

    private String title;

    private Integer price;

    private String link;

    private String image;

    private String memo;

    @Builder.Default
    private Boolean interestedItem = Boolean.FALSE;

    @Builder.Default
    private Boolean secretItem = Boolean.FALSE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus = ItemStatus.POST;

    @Builder.Default
    @OneToMany(mappedBy = "item")
    private List<ItemUser> itemUserList = new ArrayList<>();

}
