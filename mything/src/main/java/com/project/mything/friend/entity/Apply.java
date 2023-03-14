package com.project.mything.friend.entity;

import com.project.mything.friend.entity.enums.ApplyStatus;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receive_user_id")
    private User receiveUser;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ApplyStatus applyStatus = ApplyStatus.SUGGEST;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "send_user_id")
    private User sendUser;
}
