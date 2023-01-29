package com.project.mything.user.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.user.entity.Avatar;
import com.project.mything.user.entity.User;
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

    public User findVerifiedUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

    public void editUserProfile(User dbUser, String name, String infoMessage, LocalDate birthDay) {
        dbUser.editProfile(name, infoMessage, birthDay);
    }

    public User uploadImage(MultipartFile multipartFile, Long userId) {
        User dbUser = userRepository.findUserWithAvatar(userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        try {
            Avatar dbAvatar = avatarService.getDbAvatar(multipartFile, dbUser);
            return dbAvatar.getUser();
        } catch (
                IOException e) {
            throw new BusinessLogicException(ErrorCode.S3_SERVICE_ERROR);
        }
    }

}