package com.project.mything.friend.repository;

import com.project.mything.friend.entity.Friend;
import com.project.mything.friend.repository.friendQueryDsl.FriendQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendQueryRepository {
    @Query("select f from Friend f left join fetch f.user where f.user.id = :userId and f.userFriend.id = :userFriendId")
    Optional<Friend> findFriend(Long userId, Long userFriendId);
}
