package com.project.mything.user.entity;

import com.project.mything.friend.entity.Apply;
import com.project.mything.friend.entity.Friend;
import com.project.mything.item.entity.ItemUser;
import com.project.mything.notice.Notice;
import com.project.mything.time.BaseTime;
import com.project.mything.user.entity.enums.UserEmoji;
import com.project.mything.user.entity.enums.UserStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
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

    private LocalDate birthDay;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;

    @Builder.Default
    private String infoMessage = "";

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
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<ItemUser> itemUserList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<BirthDay> birthDays = new ArrayList<>();

    public void editProfile(String userName, String userInfoMessage, LocalDate userBirthDay) {
        name = userName;
        infoMessage = userInfoMessage;
        birthDay = userBirthDay;
    }

    public void addAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public void deleteAvatar() {
        this.avatar = null;
    }
}
