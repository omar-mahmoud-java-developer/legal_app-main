package com.omar.legal_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omar.legal_app.entity.Comments;
@Repository
public interface CommentsRepository extends JpaRepository<Comments, Integer>  {
    
}
