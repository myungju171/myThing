package com.project.mything.friend.entity;

import com.project.mything.friend.entity.enums.ApplyStatus;
import com.project.mything.friend.entity.enums.ApplyType;
import com.project.mything.time.BaseTime;
import com.project.mything.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Apply extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    private Long id;

    private Long receiveUserId;

    @Enumerated(EnumType.STRING)
    private ApplyType applyType;

    @Enumerated(EnumType.STRING)
    private ApplyStatus applyStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User sendUser;
}
