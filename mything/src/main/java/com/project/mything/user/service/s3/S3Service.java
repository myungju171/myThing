package com.project.mything.user.service.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    public abstract void uploadImage(MultipartFile multipartFile, String phone);

    public abstract String getRemotePath(String originalName);

    public abstract String getLocalPath(String originalName, String phone);

}
