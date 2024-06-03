package com.omar.legal_app.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.omar.legal_app.entity.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
   // Optional<User>findByEmail (String email);
	//User findByEmail (String email);

@Modifying
@Transactional
	@Query("update User u set u.password =?2 where u.email=?1")
	void  updatePassword(String email , String password);
}
