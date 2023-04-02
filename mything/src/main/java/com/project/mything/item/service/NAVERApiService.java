package com.project.mything.item.service;

import com.project.mything.item.dto.ItemDto;


public interface NAVERApiService {
    public abstract ItemDto.ResponseSearchItem searchItem(String query, Integer size, String sort, Integer start);
}
