package com.project.mything.item.service;

import org.springframework.http.ResponseEntity;


public interface NAVERApiService {
    public abstract ResponseEntity<String> searchItem(String query, Integer size, String sort, Integer start);
}
