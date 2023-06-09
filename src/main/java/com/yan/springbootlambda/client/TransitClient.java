package com.yan.springbootlambda.client;

import com.google.transit.realtime.GtfsRealtime;
import com.yan.springbootlambda.exception.TransitClientException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransitClient {

    public static final String MTA_TRANSIT_BASE_URL = "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2F";
    public static final String MTA_TRANSIT_API_KEY = System.getenv("MTA_TRANSIT_API_KEY");

    public List<GtfsRealtime.TripUpdate> fetchTransitSchedule(Character train) throws TransitClientException {
        URL url;
        try {
            System.out.println("---fetch new time with api---");
            url = new URL(MTA_TRANSIT_BASE_URL + urlPath(train));
            HttpURLConnection myURLConnection = (HttpURLConnection) url.openConnection();
            myURLConnection.setRequestProperty("X-API-KEY", MTA_TRANSIT_API_KEY);
            GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(myURLConnection.getInputStream());

            return feed.getEntityList().stream()
                    .map(GtfsRealtime.FeedEntity::getTripUpdate)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            throw new TransitClientException("Exception when calling mta url", e);
        }
    }

    private String urlPath(Character train) {
        return switch (Character.toLowerCase(train)) {
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
