package com.project.mything.user.entity;

import com.project.mything.friend.entity.Apply;
import com.project.mything.friend.entity.Friend;
import com.project.mything.item.entity.ItemUser;
import com.project.mything.notice.Notice;
import com.project.mything.auth.service.PasswordService;
import com.project.mything.time.BaseTime;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.enums.UserStatus;
import com.project.mything.image.entity.Image;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;

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

    private String email;

    private String password;
    private String name;

    private String phone;

    private LocalDate birthday;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;

    @Builder.Default
    private String infoMessage = "";

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = {PERSIST, REMOVE})
    private List<UserRole> userRoles = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = REMOVE)
    private List<Friend> friendList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "sendUser", cascade = REMOVE)
    private List<Apply> applyList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "receiveUser")
    private List<Notice> noticeList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = REMOVE)
    private List<ItemUser> itemUserList = new ArrayList<>();

    public void editProfile(UserDto.RequestEditProFile requestEditProFile) {
        name = requestEditProFile.getName();
        infoMessage = requestEditProFile.getInfoMessage();
        birthday = requestEditProFile.getBirthday();
    }

    public void addImage(Image image) {
        this.image = image;
    }

    public void encodePassword(PasswordService passwordService) {
        password = passwordService.encodePassword(password);
    }

    public void removeImage() {
        this.image = null;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}
