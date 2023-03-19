package com.project.mything.user.service.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    public abstract String uploadImage(MultipartFile multipartFile);

    public abstract String getRemotePath(String originalName);

    public abstract String getLocalPath(String originalName);

    public abstract void deleteImage(String localPath);
}
