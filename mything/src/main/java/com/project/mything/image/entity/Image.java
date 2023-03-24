package com.project.mything.image.entity;

import com.project.mything.time.BaseTime;
import com.project.mything.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    private String originalFilename;

    private Integer fileSize;

    private String localPath;

    private String remotePath;

    @OneToOne(mappedBy = "image")
    private User user;

    public void mappingToUser(User dbUser) {
        user = dbUser;
        dbUser.addImage(this);
    }
}
