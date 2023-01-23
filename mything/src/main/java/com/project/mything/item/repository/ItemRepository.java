package com.project.mything.item.repository;

import com.project.mything.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ItemRepository extends JpaRepository<Item,Long> {
}
