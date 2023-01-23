package com.project.mything.item.entity;

import com.project.mything.item.entity.enums.ItemStatus;
import com.project.mything.time.BaseTime;
import com.project.mything.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemUser extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_user_id")
    private Long id;

    private String memo;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus = ItemStatus.POST;

    @Builder.Default
    private Boolean interestedItem = Boolean.FALSE;

    @Builder.Default
    private Boolean secretItem = Boolean.FALSE;

    private Long reservedUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "item_id")
    private Item item;

    public ItemUser addItemUser() {
        if (!user.getItemUserList().contains(this)) {
            user.getItemUserList().add(this);
        }
        if (!item.getItemUserList().contains(this)) {
            item.getItemUserList().add(this);
        }
        return this;
    }

    public void changeItemStatus(ItemStatus itemStatus, Long reservedUserId) {
        this.itemStatus = itemStatus;
        this.reservedUserId = reservedUserId;
    }

    public void cancelReserveItem() {
        this.itemStatus = ItemStatus.POST;
        this.reservedUserId = null;
    }
}
