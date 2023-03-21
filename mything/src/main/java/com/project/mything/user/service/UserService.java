package com.project.mything.user.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.Image;
import com.project.mything.user.entity.User;
import com.project.mything.user.mapper.UserMapper;
import com.project.mything.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ImageService imageService;
    private final UserMapper userMapper;

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
        if (requestEditProFile.getAvatar() != null) {
            Image dbImage = imageService.findById(requestEditProFile.getAvatar().getImageId());
            dbImage.addUser(dbUser);
            dbUser.addImage(dbImage);
        }
    }

    public User findUserWithAvatar(Long userId) {
        return userRepository.findUserWithImage(userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

    public User findUserWithItemUserByPhone(String phone) {
        return userRepository.findUserWithItemUserByPhone(phone)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

    public UserDto.ResponseDetailUser getUserInfo(Long userId) {
        User dbUser = findUserWithAvatar(userId);
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
}
