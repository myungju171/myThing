package com.project.mything.user.entity;

import com.project.mything.time.BaseTime;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Avatar extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "avatar_id")
    private Long id;

    private String originalFilename;

    private Integer fileSize;

    private String localPath;

    private String remotePath;

    @OneToOne(mappedBy = "avatar")
    private User user;

}
