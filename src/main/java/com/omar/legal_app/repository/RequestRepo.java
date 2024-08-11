package com.omar.legal_app.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.omar.legal_app.entity.RequestEntity;
import com.omar.legal_app.entity.User;

@Repository
public interface RequestRepo extends JpaRepository<RequestEntity, Integer> {
   List<RequestEntity> findByUsers(User user);

  @Query("SELECT new map(FUNCTION('DATE', r.requestDate) as date, COUNT(r) as count) FROM RequestEntity r GROUP BY FUNCTION('DATE', r.requestDate) ORDER BY date")
  public List<Map<String, Object>> findRequestCountsPerDay();

  @Query("SELECT new map(r.response as response, COUNT(r) as count) FROM RequestEntity r GROUP BY r.response")
  public List<Map<String, Object>> findRequestCountsByResponse();
}
