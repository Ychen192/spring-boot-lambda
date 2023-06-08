package com.yan.springbootlambda.service;

import com.yan.springbootlambda.model.TransitData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class Component {

    @Bean
    public Map<String, TransitData> transitTimeCache() {
        return new HashMap<>();
    }
}
