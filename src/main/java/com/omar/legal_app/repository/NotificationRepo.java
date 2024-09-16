package com.omar.legal_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.omar.legal_app.entity.NotificationeEntity;


public interface NotificationRepo extends JpaRepository<NotificationeEntity, Integer> {
    
}
