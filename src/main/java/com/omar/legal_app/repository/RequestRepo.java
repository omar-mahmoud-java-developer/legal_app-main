package com.omar.legal_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.omar.legal_app.entity.RequestEntity;
import com.omar.legal_app.entity.User;

public interface RequestRepo extends JpaRepository<RequestEntity, Integer> {
   List<RequestEntity> findByUsers(User user);
    

}
