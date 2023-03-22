package com.project.mything.friend.entity;

import com.project.mything.friend.entity.enums.FriendStatus;
import com.project.mything.time.BaseTime;
import com.project.mything.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_friend_id")
    private User userFriend;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private FriendStatus friendStatus = FriendStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
