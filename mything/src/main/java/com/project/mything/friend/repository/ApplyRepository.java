package com.project.mything.friend.repository;

import com.project.mything.friend.entity.Apply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

    @Query("select a from Apply a left join fetch a.sendUser where a.sendUser = :sendUserId")
    Optional<Apply> findApplyWithSendUserById(Long sendUserId);

    @Query("select a from Apply a left join fetch a.receiveUser where a.receiveUser = :receiveUserId")
    Optional<Apply> findApplyWithReceiveUserById(Long receiveUserId);
}
