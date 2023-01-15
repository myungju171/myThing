package com.project.mything.user.entity;

import com.project.mything.friend.entity.Apply;
import com.project.mything.friend.entity.Friend;
import com.project.mything.item.entity.Item;
import com.project.mything.notice.Notice;
import com.project.mything.time.BaseTime;
import com.project.mything.user.entity.enums.UserEmoji;
import com.project.mything.user.entity.enums.UserStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;

    private String phone;

    private LocalDateTime birthDay;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    private String infoMessage;

    @Enumerated(EnumType.STRING)
    private UserEmoji userEmoji;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Friend> friendList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "sendUser")
    private List<Apply> applyList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "receiveUser")
    private List<Notice> noticeList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Item> itemList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<BirthDay> birthDays = new ArrayList<>();
}
