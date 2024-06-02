package com.omar.legal_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.omar.legal_app.entity.ForgotPassword;

public interface ForgotRepo extends JpaRepository<ForgotPassword, Integer> {
    
}
