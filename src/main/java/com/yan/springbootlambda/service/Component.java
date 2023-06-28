package com.yan.springbootlambda.service;

import com.yan.springbootlambda.model.TransitData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Configuration
public class Component {

    @Bean
    public Map<String, TransitData> transitTimeCache() {
        return new HashMap<>();
    }

    @Bean
    Map<String, String> transitStopCache() {
        Map<String, String> transitStops = new HashMap<>();
        try (InputStream inputStream = getClass().getResourceAsStream("/stops.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.lines()
                    .map(line -> line.split(","))
                    .forEach(line -> transitStops.put(line[0], line[2]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transitStops;
    }
}
