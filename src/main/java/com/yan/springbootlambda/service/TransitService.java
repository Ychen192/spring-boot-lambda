package com.yan.springbootlambda.service;

import com.yan.springbootlambda.client.TransitClient;
import com.yan.springbootlambda.exception.TransitClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransitService {
    @Autowired
    private TransitClient transitClient;

    public String fetchTransitTime() throws TransitClientException {
        return transitClient.fetchTransitSchedule();
    }

    public Map<String, String> fetchTransitStops() {
        Map<String, String> stops = new HashMap<>();
        BufferedReader reader;
        String line;

        try {
            reader = new BufferedReader(new FileReader("src/main/resources/stops.txt"));
            line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                var words = line.split(",");
                stops.put(words[0], words[2]);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stops;
    }

}
