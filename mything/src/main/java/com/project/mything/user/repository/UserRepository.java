package com.project.mything.user.repository;

import com.project.mything.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByPhone(String phone);

    @Query("select u from User u left join fetch u.itemUserList where u.phone = :phone")
    Optional<User> findUserWithItemUserByPhone(String phone);

    @Query("select u from User u left join fetch u.avatar where u.id = :userId")
    Optional<User> findUserWithAvatar(Long userId);

    Optional<User> findByEmail(String email);
}
