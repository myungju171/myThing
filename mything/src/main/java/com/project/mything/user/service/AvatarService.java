package com.project.mything.user.service;

import com.project.mything.user.entity.Avatar;
import com.project.mything.user.entity.User;
import com.project.mything.user.repository.AvatarRepository;
import com.project.mything.user.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class AvatarService {

    private final S3Service s3Service;
    private final AvatarRepository avatarRepository;

    public Avatar getDbAvatar(MultipartFile multipartFile, User user) throws IOException {
        if (user.getAvatar() != null) {
            avatarRepository.delete(user.getAvatar());
        }
        s3Service.uploadImage(multipartFile, user.getPhone());

        return saveAvatar(multipartFile, user);
    }

    private Avatar saveAvatar(MultipartFile multipartFile, User user) {
        String originalName = multipartFile.getOriginalFilename();
        Integer size = (int) multipartFile.getSize();
        String localPath = s3Service.getLocalPath(originalName, user.getPhone());
        String remotePath = s3Service.getRemotePath(localPath);

        Avatar avatar = Avatar.builder()
                .originalFilename(originalName)
                .localPath(localPath)
                .fileSize(size)
                .remotePath(remotePath)
                .user(user)
                .build();

        avatar.getUser().addAvatar(avatar);

        return avatarRepository.save(avatar);
    }
}
