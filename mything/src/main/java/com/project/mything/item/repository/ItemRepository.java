package com.project.mything.item.repository;

import com.project.mything.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ItemRepository extends JpaRepository<Item,Long> {

    Optional<Item> findItemByProductId(Long productId);
}
