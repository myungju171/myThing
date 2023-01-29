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

    public void editUserProfile(Long userId, String name, String infoMessage, LocalDate birthDay) {
        User dbUser = findVerifiedUser(userId);
        dbUser.editProfile(name, infoMessage, birthDay);
    }

    public String uploadImage(MultipartFile multipartFile, Long userId) {
        User dbUser = findVerifiedUser(userId);

        try {
            Avatar dbAvatar = avatarService.getDbAvatar(multipartFile, dbUser);
            return dbAvatar.getRemotePath();
        } catch (
                IOException e) {
            throw new BusinessLogicException(ErrorCode.S3_SERVICE_ERROR);
        }
    }

}
