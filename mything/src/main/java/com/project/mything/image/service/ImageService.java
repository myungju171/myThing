package com.project.mything.image.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.image.dto.ImageDto;
import com.project.mything.image.mapper.ImageMapper;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import com.project.mything.user.repository.UserRepository;
import com.project.mything.image.entity.Image;
import com.project.mything.image.repository.ImageRepository;
import com.project.mything.image.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private final S3Service s3Service;
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final UserRepository userRepository;

    public ImageDto.SimpleImageDto uploadImage (MultipartFile multipartFile) {
        String localPath = getLocalPath(multipartFile);

        Image image = imageMapper.toImage(localPath,
                multipartFile.getOriginalFilename(),
                (int) multipartFile.getSize(),
                getRemotePath(localPath));
        return imageMapper.toResponseUpload(imageRepository.save(image));
    }

    private String getLocalPath(MultipartFile multipartFile) {
        return s3Service.uploadImage(multipartFile);
    }

    private String getRemotePath(String localPath) {
        return s3Service.getRemotePath(localPath);
    }

    public void deleteImage(UserDto.UserInfo userInfo) {
        User userWithAvatar = userRepository.findUserWithImage(userInfo.getUserId())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        try {
            Image image = userWithAvatar.getImage();
            s3Service.deleteImage(image.getLocalPath());
            imageRepository.delete(image);
        } catch (NullPointerException e) {
            throw new BusinessLogicException(ErrorCode.AVATAR_MUST_NOT_NULL);
        }
        userWithAvatar.removeImage();
    }

    public Image findById(Long imageId) {
        return imageRepository.findById(imageId).orElseThrow(()->new BusinessLogicException(ErrorCode.IMAGE_NOT_FOUND));
    }
}
