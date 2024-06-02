package com.omar.legal_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.omar.legal_app.entity.RequestEntity;

public interface RequestRepo extends  JpaRepository<RequestEntity, Integer> {
    
}
