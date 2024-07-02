package com.omar.legal_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.omar.legal_app.entity.CustomerEntity;

public interface CustomerRepo extends JpaRepository<CustomerEntity, Integer> {
    


    List<CustomerEntity> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String query, String query0);}
