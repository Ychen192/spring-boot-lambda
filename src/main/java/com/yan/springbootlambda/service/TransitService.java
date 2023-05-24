package com.yan.springbootlambda.service;

import com.yan.springbootlambda.client.TransitClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransitService {
    @Autowired
    private TransitClient transitClient;

    public String fetchTransitTime(){
        return transitClient.fetchTransitSchedule();
    }
}
