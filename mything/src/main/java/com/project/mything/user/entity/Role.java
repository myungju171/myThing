package com.project.mything.user.entity;

import com.project.mything.user.entity.enums.RoleName;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private RoleName roleName = RoleName.ROLE_USER;

    @Builder.Default
    @OneToMany(mappedBy = "role", cascade = CascadeType.PERSIST)
    private List<UserRole> userRoles = new ArrayList<>();

}
