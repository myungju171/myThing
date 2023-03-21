package com.project.mything.friend.repository;

import com.project.mything.friend.entity.Friend;
import com.project.mything.friend.repository.friendQueryDsl.FriendQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendQueryRepository {
}
