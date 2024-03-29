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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_user_id")
    private User reservedUser;

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

    public void updateItemStatus(ItemStatus itemStatus, User reservedUser) {
        this.itemStatus = itemStatus;
        if(reservedUser != null) this.reservedUser = reservedUser;
    }

    public void cancelReserveItem() {
        this.reservedUser = null;
        this.itemStatus = ItemStatus.POST;
    }

    public void changeItemInterest() {
        this.interestedItem =
                this.interestedItem ? Boolean.FALSE : Boolean.TRUE;
    }

    public void changeItemSecret() {
        this.secretItem =
                this.secretItem ? Boolean.FALSE : Boolean.TRUE;
    }

}
