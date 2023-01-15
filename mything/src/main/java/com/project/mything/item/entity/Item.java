package com.project.mything.item.entity;

import com.project.mything.item.entity.enums.ItemEmoji;
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
public class Item extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private String name;

    private Integer price;

    private String url;

    private String memo;

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    @Enumerated(EnumType.STRING)
    private ItemEmoji itemEmoji;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_Id")
    private File file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
