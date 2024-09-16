package com.omar.legal_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.omar.legal_app.repository.RequestRepo;

@Service
public class RequestService {

    @Autowired
    private RequestRepo requestRepo;

    public Integer getTotalRequests() {
        return requestRepo.countTotalRequests();
    }

  
}