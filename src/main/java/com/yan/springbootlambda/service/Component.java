package com.yan.springbootlambda.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class Component {

    public static final int CACHE_SIZE = 10;
    public static final long ZERO = 0L;

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Map<Instant, List<String>> transitTimeCache() {
        var cache = new LinkedHashMap<Instant, List<String>>(CACHE_SIZE) {
            protected boolean removeEldestEntry(Map.Entry<Instant, List<String>> eldest) {
                return size() > CACHE_SIZE;
            }
        };
        cache.put(Instant.ofEpochMilli(ZERO), new ArrayList<>());
        return cache;
    }
}
