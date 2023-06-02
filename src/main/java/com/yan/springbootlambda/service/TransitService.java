package com.yan.springbootlambda.service;

import com.yan.springbootlambda.client.TransitClient;
import com.yan.springbootlambda.exception.TransitClientException;
import com.yan.springbootlambda.model.TrainStation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransitService {
    @Autowired
    private TransitClient transitClient;

    public List<String> fetchTransitTime(String stationId) throws TransitClientException {
        return transitClient.fetchTransitSchedule().stream()
                .filter(it -> it.getStopId().contains(stationId))
                .map(it -> it.getArrival().getTime())
                .map(it -> Instant.ofEpochSecond(it).atZone(ZoneId.of("America/New_York")))
                .map(it -> it.format(DateTimeFormatter.ofPattern("MM/dd/uuuu HH:mm:ss")))
                .collect(Collectors.toList());
    }

    public List<TrainStation> fetchTransitStops(Character trainLine) {
        Map<String, TrainStation> stops = new HashMap<>();
        try (InputStream inputStream = getClass().getResourceAsStream("/stops.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.lines()
                    .map(line -> line.split(","))
                    .forEach(line -> stops.put(line[0], new TrainStation(line[0], line[2])));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stops.entrySet()
                .stream()
                .filter(it -> it.getKey().charAt(0) == Character.toUpperCase(trainLine))
                .filter(it -> it.getKey().charAt(it.getKey().length() - 1) >= 48 && it.getKey().charAt(it.getKey().length() - 1) <= 57)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
