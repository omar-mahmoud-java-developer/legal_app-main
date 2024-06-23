package com.omar.legal_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.omar.legal_app.entity.Comments;

public interface CommentsRepository extends JpaRepository<Comments, Integer>  {
    
}
