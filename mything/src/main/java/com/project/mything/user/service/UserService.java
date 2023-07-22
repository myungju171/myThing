package com.project.mything.user.service;

import com.project.mything.auth.service.PasswordService;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import com.project.mything.user.mapper.UserMapper;
import com.project.mything.user.repository.UserRepository;
import com.project.mything.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ImageService imageService;
    private final UserMapper userMapper;
    private final PasswordService passwordService;

    public User findVerifiedUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

    public UserDto.ResponseDetailUser editProFile(UserDto.UserInfo userInfo,
                                                  UserDto.RequestEditProFile requestEditProFile) {
        User dbUser = findUserWithAvatar(userInfo.getUserId());
        dbUser.editProfile(requestEditProFile);
        editAvatar(requestEditProFile, dbUser);
        return userMapper.toResponseDetailUser(dbUser);
    }

    private void editAvatar(UserDto.RequestEditProFile requestEditProFile, User dbUser) {
        if (requestEditProFile.getAvatar() == null)
            imageService.deleteImage(dbUser.getId());
        else imageService.findById(Objects.requireNonNull(requestEditProFile.getAvatar()).getImageId())
                .mappingToUser(dbUser);
    }

    public User findUserWithAvatar(Long userId) {
        return userRepository.findUserWithImage(userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

    public User findUserWithItemUserByPhone(String phone) {
        return userRepository.findUserWithItemUserByPhone(phone)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

    public User findUserByPhone(String phone) {
        return userRepository.findUserByPhone(phone)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

    public boolean findByPhone(String phone) {
        return userRepository.findUserByPhone(phone).isPresent();
    }

    public UserDto.ResponseDetailUser getUserDetail(UserDto.UserInfo userInfo) {
        User dbUser = findUserWithAvatar(userInfo.getUserId());
        return userMapper.toResponseDetailUser(dbUser);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.NO_CORRECT_ACCOUNT));
    }

    public Boolean duplicateEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void withdrawal(UserDto.UserInfo userInfo) {
        User dbUser = findUserWithAvatar(userInfo.getUserId());
        if (dbUser.getImage() != null)
            imageService.deleteImage(dbUser.getImage().getId());
        userRepository.delete(dbUser);
    }

    public void changePassword(UserDto.UserInfo userInfo, UserDto.RequestChangePassword requestChangePassword) {
        User dbUser = findVerifiedUser(userInfo.getUserId());
        passwordService.validatePassword(requestChangePassword.getOriginalPassword(), dbUser.getPassword());
        dbUser.changePassword(requestChangePassword.getNewPassword());
        dbUser.encodePassword(passwordService);
    }
}
