package com.omar.legal_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.omar.legal_app.entity.ForgotPassword;
import com.omar.legal_app.entity.User;

public interface ForgotRepo extends JpaRepository<ForgotPassword, Integer> {

    @Query("select fp from ForgotPassword fp where fp.code = ?1 and fp.user = ?2")
    Optional<ForgotPassword> findByCodeAndUser(Integer code, User user);


    
    
}
