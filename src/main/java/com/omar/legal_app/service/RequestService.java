package com.omar.legal_app.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.omar.legal_app.repository.RequestRepo;

@Service
public class RequestService {

    @Autowired
    private RequestRepo requestRepo;

    public List<Map<String, Object>> getRequestCountsPerDay() {
        List<Map<String, Object>> requestCounts = requestRepo.findRequestCountsPerDay();
        return requestCounts;
    }
}
