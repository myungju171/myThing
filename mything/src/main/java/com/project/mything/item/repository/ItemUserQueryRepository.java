package com.project.mything.item.repository;

import com.project.mything.item.dto.ItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemUserQueryRepository {
    public abstract Page<ItemDto.ResponseSimpleItem> searchSimpleItem(Long userId, Boolean isWish, Boolean isFriend, String sortBy, Pageable pageable);
}
