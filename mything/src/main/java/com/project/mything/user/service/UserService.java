package com.project.mything.user.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.Avatar;
import com.project.mything.user.entity.User;
import com.project.mything.user.mapper.UserMapper;
import com.project.mything.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AvatarService avatarService;
    private final UserMapper userMapper;

    public User findVerifiedUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

    public UserDto.ResponseImageURl uploadImageAndEditUserProfile(MultipartFile multipartFile,
                                                                  Long userId,
                                                                  String name,
                                                                  String infoMessage,
                                                                  LocalDate birthDay) {
        User dbUser = uploadImage(multipartFile, userId);
        return editUserProfile(dbUser, name, infoMessage, birthDay);
    }

    private UserDto.ResponseImageURl editUserProfile(User dbUser,
                                                     String name,
                                                     String infoMessage,
                                                     LocalDate birthDay) {
        dbUser.editProfile(name, infoMessage, birthDay);
        return userMapper.toResponseImageUrl(dbUser);
    }

    private User uploadImage(MultipartFile multipartFile, Long userId) {
        User dbUser = findUserWithAvatar(userId);
        try {
            if (!multipartFile.isEmpty()) {
                Avatar dbAvatar = avatarService.getDbAvatar(multipartFile, dbUser);
                return dbAvatar.getUser();
            }
        } catch (
                IOException e) {
            throw new BusinessLogicException(ErrorCode.S3_SERVICE_ERROR);
        }
        return dbUser;
    }

    private User findUserWithAvatar(Long userId) {
        return userRepository.findUserWithAvatar(userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

    }

    public void deleteAvatar(Long userId) {
        User dbUser = findUserWithAvatar(userId);
        checkAvatarIsNotNull(dbUser);
        avatarService.deleteAvatar(dbUser.getAvatar());
    }

    private void checkAvatarIsNotNull(User dbUser) {
        if (dbUser.getAvatar() == null) {
            throw new BusinessLogicException(ErrorCode.AVATAR_MUST_NOT_NULL);
        }
    }
}
