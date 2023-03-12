package com.project.mything.user.service.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.user.config.S3Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final AmazonS3Client amazonS3Client;
    private final S3Config s3Config;

    @Override
    public void uploadImage(MultipartFile multipartFile, String phone)  {
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

        amazonS3Client.putObject(
                new PutObjectRequest(s3Config.getS3Bucket(), getLocalPath(originalName, phone), inputStream, objectMetaData)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    @Override
    public String getRemotePath(String localPath) {
        return amazonS3Client.getUrl(s3Config.getS3Bucket(), localPath).toString();
    }

    @Override
    public String getLocalPath(String originalName, String phone) {
        String png = originalName.substring(originalName.lastIndexOf("."));
        return phone + png;
    }

}
