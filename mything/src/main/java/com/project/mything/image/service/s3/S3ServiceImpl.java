package com.project.mything.image.service.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.image.config.S3Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final AmazonS3Client amazonS3Client;
    private final S3Config s3Config;

    @Override
    public String uploadImage(MultipartFile multipartFile)  {
        String originalName = multipartFile.getOriginalFilename();
        long size =  multipartFile.getSize();

        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(multipartFile.getContentType());
        objectMetaData.setContentLength(size);

        InputStream inputStream;
        try {
            inputStream = multipartFile.getInputStream();
        } catch (IOException ioException) {
            throw new BusinessLogicException(ErrorCode.S3_SERVICE_ERROR);
            //에러로그 전송하기 남기기
        }
        String localPath = getLocalPath(Objects.requireNonNull(originalName));
        amazonS3Client.putObject(
                new PutObjectRequest(s3Config.getS3Bucket(), localPath, inputStream, objectMetaData)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
        return localPath;
    }

    @Override
    public String getRemotePath(String localPath) {
        return amazonS3Client.getUrl(s3Config.getS3Bucket(), localPath).toString();
    }

    @Override
    public String getLocalPath(String originalName) {
        String png = originalName.substring(originalName.lastIndexOf("."));
        return UUID.randomUUID() + png;
    }

    @Override
    public void deleteImage(String localPath) {
        amazonS3Client.deleteObject(s3Config.getS3BucketName(), "mything/"+localPath);
    }
}
