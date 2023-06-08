package com.yan.springbootlambda.service;

import com.yan.springbootlambda.model.TransitData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

@Configuration
public class Component {

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Map<String, TransitData> transitTimeCache() {
        return new HashMap<>();
    }
}
