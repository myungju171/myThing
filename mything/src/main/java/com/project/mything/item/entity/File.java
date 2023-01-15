package com.project.mything.item.entity;

import com.project.mything.time.BaseTime;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    private String originalFilename;

    private Integer fileSize;

    private String localPath;

    private String remotePath;

    @OneToOne(mappedBy = "file")
    private Item item;

}
