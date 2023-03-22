package com.project.mything.friend.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.friend.dto.ApplyDto;
import com.project.mything.friend.entity.Apply;
import com.project.mything.friend.entity.enums.ApplyStatus;
import com.project.mything.friend.mapper.ApplyMapper;
import com.project.mything.friend.repository.ApplyRepository;
import com.project.mything.friend.repository.applyQueryDsl.ApplyQueryRepository;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import com.project.mything.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ApplyService {

    private final ApplyRepository applyRepository;
    private final FriendService friendService;
    private final UserService userService;
    private final ApplyMapper applyMapper;
    private final ApplyQueryRepository applyQueryRepository;

    public ApplyDto.ResponseApplyId createApply(UserDto.UserInfo userInfo, Long receiveUserId) {
        User sendUser = userService.findVerifiedUser(userInfo.getUserId());
        User receiveUser = userService.findVerifiedUser(receiveUserId);
        Apply dbApply = saveApply(sendUser, receiveUser);
        return applyMapper.toResponseSimpleApply(dbApply.getId());
    }

    private Apply saveApply(User sendUser, User receiveUser) {
        Apply apply = applyMapper.ToApply(sendUser, receiveUser);
        sendUser.getApplyList().add(apply);
        receiveUser.getApplyList().add(apply);
        return applyRepository.save(apply);
    }

    public void cancelApply(UserDto.UserInfo userInfo, Long applyId) {
        Apply dbApply = findApplyWithSendUserById(applyId);
        verifyApplySender(userInfo.getUserId(), dbApply);
        applyRepository.delete(dbApply);
    }

    public void rejectApply(UserDto.UserInfo userInfo, Long applyId) {
        Apply dbApply = findApplyWithReceiveUserById(applyId);
        verifyApplyReceiver(userInfo.getUserId(), dbApply);
        applyRepository.delete(dbApply);
    }


    private Apply findApplyWithSendUserById(Long applyId) {
        return applyRepository.findApplyWithSendUserById(applyId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.APPLY_NOT_FOUND));
    }

    private Apply findApplyWithReceiveUserById(Long applyId) {
        return applyRepository.findApplyWithReceiveUserById(applyId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.APPLY_NOT_FOUND));
    }

    private void verifyApplySender(Long senderId, Apply apply) {
        if (!senderId.equals(apply.getSendUser().getId()))
            throw new BusinessLogicException(ErrorCode.APPLY_SENDER_CONFLICT);
    }

    private void verifyApplyReceiver(Long receiverId, Apply apply) {
        if (!receiverId.equals(apply.getReceiveUser().getId()))
            throw new BusinessLogicException(ErrorCode.APPLY_RECEIVER_CONFLICT);
    }

    private Apply findVerifyApply(Long applyId) {
        return applyRepository.findById(applyId).orElseThrow(() -> new BusinessLogicException(ErrorCode.APPLY_NOT_FOUND));
    }

    public void acceptApply(UserDto.UserInfo userInfo, Long applyId) {
        Apply dbApply = findApplyWithReceiveUserById(applyId);
        verifyApplyReceiver(userInfo.getUserId(), dbApply);
        friendService.createFriend(dbApply.getSendUser().getId(), dbApply.getReceiveUser().getId());
        deleteApply(dbApply);
    }

    private void deleteApply(Apply dbApply) {
        dbApply.getReceiveUser().getApplyList().remove(dbApply);
        dbApply.getSendUser().getApplyList().remove(dbApply);
        applyRepository.delete(dbApply);
    }

    @Transactional(readOnly = true)
    public List<ApplyDto.ResponseSimpleApply> getApply(UserDto.UserInfo userInfo, Boolean isReceiveApply, ApplyStatus applyStatus) {
        return applyQueryRepository.getApply(userInfo.getUserId(), isReceiveApply);
    }
}
