package com.yan.springbootlambda.client;

import com.google.transit.realtime.GtfsRealtime;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransitClient {

    public static final String MTA_TRANSIT_URL = "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-ace";
    public static final String MTA_TRANSIT_API_KEY = "x4jPVKUJ8K2bq8jSyhoIf4bA0HiwAR939fcUXTUZ";

    public String fetchTransitSchedule() {
        URL url;
        try {
            url = new URL(MTA_TRANSIT_URL);
            HttpURLConnection myURLConnection = (HttpURLConnection) url.openConnection();
            myURLConnection.setRequestProperty("X-API-KEY", MTA_TRANSIT_API_KEY);

            GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(myURLConnection.getInputStream());

            List<GtfsRealtime.TripUpdate> list = feed.getEntityList().stream()
                    .map(GtfsRealtime.FeedEntity::getTripUpdate)
                    .collect(Collectors.toList());
            return list.get(0).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
