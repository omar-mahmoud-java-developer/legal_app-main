package com.omar.legal_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omar.legal_app.entity.ForgotPasswordToken;

@Repository
public interface ForgotPasswordRepo extends  JpaRepository<ForgotPasswordToken, Long> {
    
    ForgotPasswordToken findByToken(String token);
}
