package com.yan.springbootlambda.client;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.transit.realtime.GtfsRealtime;
import com.yan.springbootlambda.exception.TransitClientException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransitClient {

    public static final String MTA_TRANSIT_BASE_URL = "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2F";
    public static final String MTA_TRANSIT_API_KEY = System.getenv("MTA_TRANSIT_API_KEY");

    public List<GtfsRealtime.TripUpdate.StopTimeUpdate> fetchTransitSchedule(String stationId) throws TransitClientException {
        URL url;
        try {
            System.out.println("---fetch new time with api---");
            url = new URL(MTA_TRANSIT_BASE_URL + urlPath(stationId));
            HttpURLConnection myURLConnection = (HttpURLConnection) url.openConnection();
            myURLConnection.setRequestProperty("X-API-KEY", MTA_TRANSIT_API_KEY);
            GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(myURLConnection.getInputStream());

            return feed.getEntityList().stream()
                    .map(it -> it.getTripUpdate().getStopTimeUpdateList())
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            throw new TransitClientException("Exception when calling mta url", e);
        }
    }

    private String urlPath(String stationId) {
        return switch (stationId.toLowerCase().charAt(0)) {
            case 'a', 'c', 'e' -> "gtfs-ace";
            case 'b', 'd', 'f', 'm' -> "gtfs-bdfm";
            case 'g' -> "gtfs-g";
            case 'j', 'z' -> "gtfs-jz";
            case 'n', 'q', 'r', 'w' -> "gtfs-nqrw";
            case 'l' -> "gtfs-l";
            case '1', '2', '3', '4', '5', '6', '7' -> "gtfs";
            case 's' -> "gtfs-si";
            default -> throw new IllegalArgumentException("Unsupported stationId");
        };
    }
}
