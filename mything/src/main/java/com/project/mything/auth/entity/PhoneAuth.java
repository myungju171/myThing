package com.project.mything.auth.entity;

import com.project.mything.time.BaseTime;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhoneAuth extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phone_auth_id")
    private Long id;

    private String phone;

    private String authNumber;

}
