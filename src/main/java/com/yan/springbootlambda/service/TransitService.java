package com.yan.springbootlambda.service;

import com.google.transit.realtime.GtfsRealtime;
import com.yan.springbootlambda.client.TransitClient;
import com.yan.springbootlambda.exception.TransitClientException;
import com.yan.springbootlambda.model.TrainStation;
import com.yan.springbootlambda.model.TransitData;
import com.yan.springbootlambda.model.TransitTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransitService {

    private final TransitClient transitClient;
    private final Map<String, TransitData> transitTimeCache;
    private final Map<String, String> transitStopCache;

    @Autowired
    public TransitService(TransitClient transitClient,
                          Map<String, TransitData> transitTimeCache,
                          Map<String, String> transitStopCache) {
        this.transitClient = transitClient;
        this.transitTimeCache = transitTimeCache;
        this.transitStopCache = transitStopCache;
    }

    public TransitTime fetchTransitTime(Character trainLine, String stationId) throws TransitClientException {
        String trainGroup = trainGrooup(trainLine);
        List<GtfsRealtime.TripUpdate> dataSet;
        if (!transitTimeCache.containsKey(trainGroup) || isStaleData(trainGroup)) {
            dataSet = new ArrayList<>(transitClient.fetchTransitSchedule(trainLine));
            transitTimeCache.put(trainGroup, new TransitData(Instant.now(), dataSet));
        } else {
            dataSet = transitTimeCache.get(trainGroup).getDataSet();
        }

        var north = dataSet.stream()
                .filter(it -> filterTripByTrainLine(it, trainLine))
                .filter(it -> filterByDirection(it, 'n'))
                .map(GtfsRealtime.TripUpdate::getStopTimeUpdateList)
                .flatMap(Collection::stream)
                .filter(it -> it.getStopId().contains(stationId))
                .map(it -> minutesLeft(it.getArrival().getTime()))
                .sorted()
                .collect(Collectors.toList());

        var south = dataSet.stream()
                .filter(it -> filterTripByTrainLine(it, trainLine))
                .filter(it -> filterByDirection(it, 's'))
                .map(GtfsRealtime.TripUpdate::getStopTimeUpdateList)
                .flatMap(Collection::stream)
                .filter(it -> it.getStopId().contains(stationId))
                .map(it -> minutesLeft(it.getArrival().getTime()))
                .sorted()
                .collect(Collectors.toList());

        return new TransitTime(north, south);
    }

    public long minutesLeft(long time) {
        return Duration.between(Instant.now(), Instant.ofEpochSecond(time)).getSeconds() / 60;

    }

    public boolean filterTripByTrainLine(GtfsRealtime.TripUpdate trip, Character line) {
        var tripId = trip.getTrip().getTripId().toLowerCase();
        return !tripId.equals("") && tripId.split("_")[1].charAt(0) == Character.toLowerCase(line);
    }

    public boolean filterByDirection(GtfsRealtime.TripUpdate trip, Character direction) {
        var tripId = trip.getTrip().getTripId().toLowerCase();
        return !tripId.equals("") && tripId.split("\\.\\.")[1].charAt(0) == Character.toLowerCase(direction);
    }

    private String trainGrooup(Character train) {
        return switch (Character.toLowerCase(train)) {
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

    public List<TrainStation> fetchTransitStops(Character trainLine) throws TransitClientException {
        String trainGroup = trainGrooup(trainLine);

        List<GtfsRealtime.TripUpdate> dataSet;
        if (!transitTimeCache.containsKey(trainGroup) || isStaleData(trainGroup)) {
            dataSet = new ArrayList<>(transitClient.fetchTransitSchedule(trainLine));
            transitTimeCache.put(trainGroup, new TransitData(Instant.now(), dataSet));
            System.out.println("inserted and cache size " + transitTimeCache.size());
        } else {
            dataSet = transitTimeCache.get(trainGroup).getDataSet();
        }

        return dataSet.stream()
                .filter(it -> filterTripByTrainLine(it, trainLine))
                .filter(it -> filterByDirection(it, 's'))
                .max(Comparator.comparing(it -> it.getStopTimeUpdateList().size())).get()
                .getStopTimeUpdateList().stream()
                .map(it -> new TrainStation(it.getStopId(), stationName(it.getStopId())))
                .collect(Collectors.toList());
    }

    public String stationName(String stopId) {
        return transitStopCache.getOrDefault(stopId, "");
    }
}
