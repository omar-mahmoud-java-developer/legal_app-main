package com.omar.legal_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omar.legal_app.entity.RequestEntity;
import com.omar.legal_app.entity.User;
@Repository
public interface RequestRepo extends JpaRepository<RequestEntity, Integer> {
   List<RequestEntity> findByUsers(User user);




 
   
    

}
