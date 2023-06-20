package com.yan.springbootlambda.service;

import com.google.transit.realtime.GtfsRealtime;
import com.yan.springbootlambda.client.TransitClient;
import com.yan.springbootlambda.exception.TransitClientException;
import com.yan.springbootlambda.model.TrainStation;
import com.yan.springbootlambda.model.TransitData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransitService {

    private final TransitClient transitClient;
    private final Map<String, TransitData> transitTimeCache;

    @Autowired
    public TransitService(TransitClient transitClient, Map<String, TransitData> transitTimeCache) {
        this.transitClient = transitClient;
        this.transitTimeCache = transitTimeCache;
    }

    public List<String> fetchTransitTime(String stationId) throws TransitClientException {
        Character train = stationId.toLowerCase().charAt(0);
        Character direction = 'n';
        String trainGroup = trainLine(train);
        List<GtfsRealtime.TripUpdate> dataSet;
        if (!transitTimeCache.containsKey(trainGroup) || isStaleData(trainGroup)) {
            dataSet = new ArrayList<>(transitClient.fetchTransitSchedule(stationId));
            transitTimeCache.put(trainGroup, new TransitData(Instant.now(), dataSet));
            System.out.println("inserted and cache size " + transitTimeCache.size());
        } else {
            dataSet = transitTimeCache.get(trainGroup).getDataSet();
        }

        return dataSet.stream()
                .filter(it -> filterByTrainLine(it, train))
                .filter(it -> filterByDirection(it, direction))
                .map(GtfsRealtime.TripUpdate::getStopTimeUpdateList)
                .flatMap(Collection::stream)
                .filter(it -> it.getStopId().contains(stationId))
                .map(it -> it.getDeparture().getTime())
                .map(it -> Instant.ofEpochSecond(it).atZone(ZoneId.of("America/New_York")))
                .map(it -> it.format(DateTimeFormatter.ofPattern("MM/dd/uuuu HH:mm:ss")))
                .collect(Collectors.toList());
    }

    public boolean filterByTrainLine(GtfsRealtime.TripUpdate trip, Character line) {
        var tripId = trip.getTrip().getTripId().toLowerCase();
        return !tripId.equals("") && tripId.charAt(tripId.length() - 4) == line;
    }

    public boolean filterByDirection(GtfsRealtime.TripUpdate trip, Character direction) {
        var tripId = trip.getTrip().getTripId().toLowerCase();
        return !tripId.equals("") && tripId.charAt(tripId.length() - 1) == direction;
    }

    private String trainLine(Character train) {
        return switch (train) {
            case 'a', 'c', 'e' -> "ace";
            case 'b', 'd', 'f', 'm' -> "bdfm";
            case 'g' -> "g";
            case 'j', 'z' -> "jz";
            case 'n', 'q', 'r', 'w' -> "nqrw";
            case 'l' -> "l";
            case '1', '2', '3', '4', '5', '6', '7' -> "1234567";
            case 's' -> "si";
            default -> throw new IllegalArgumentException("Unsupported stationId");
        };
    }

    private Boolean isStaleData(String line) {
        var currentTime = Instant.now();
        var lastFetchedTime = transitTimeCache.get(line).getTimeFetched();
        var timePassed = Duration.between(lastFetchedTime, currentTime).getSeconds();
        System.out.println("Line: " + line + "  Time passed: " + timePassed);
        return timePassed > 30;
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
