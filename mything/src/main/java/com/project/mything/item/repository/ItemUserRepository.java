package com.project.mything.item.repository;

import com.project.mything.item.entity.ItemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ItemUserRepository extends JpaRepository<ItemUser, Long>, ItemUserQueryRepository{

    @Query("select iu from ItemUser iu left join fetch iu.user left join fetch iu.item " +
            "where iu.user.id = :userId and iu.item.productId = :productId")
    Optional<ItemUser> findItemUserByUserIdAndProductId(Long userId, Long productId);

    @Query("select iu from ItemUser iu left join fetch iu.user left join fetch iu.item " +
            "where iu.user.id = :userId and iu.item.id = :itemId")
    Optional<ItemUser> findItemUserByUserIdAndItemId(Long userId, Long itemId);

}
