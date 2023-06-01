package com.yan.springbootlambda.service;

import com.yan.springbootlambda.client.TransitClient;
import com.yan.springbootlambda.exception.TransitClientException;
import com.yan.springbootlambda.model.TrainStation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        BufferedReader reader;
        String line;

        try {
            reader = new BufferedReader(new FileReader("src/main/resources/stops.txt"));
            line = reader.readLine();
            while (line != null) {
                var words = line.split(",");
                stops.put(words[0], new TrainStation(words[0], words[2]));
                line = reader.readLine();
            }
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
