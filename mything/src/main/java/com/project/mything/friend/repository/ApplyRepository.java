package com.project.mything.friend.repository;

import com.project.mything.friend.entity.Apply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

    @Query("select a from Apply a left join fetch a.sendUser where a.sendUser = :applyId")
    Optional<Apply> findApplyWithSendUserById(Long applyId);

    @Query("select a from Apply a left join fetch a.receiveUser where a.id = :applyId")
    Optional<Apply> findApplyWithReceiveUserById(Long applyId);

    @Query("select a from Apply a where a.sendUser.id = :receiveUserId and a.receiveUser.id = :userId")
    Optional<Apply> findExistApply(Long receiveUserId, Long userId);
}
