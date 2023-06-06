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

    public static final String MTA_TRANSIT_URL = "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-bdfm";
    public static final String MTA_TRANSIT_API_KEY = "x4jPVKUJ8K2bq8jSyhoIf4bA0HiwAR939fcUXTUZ";

    public List<GtfsRealtime.TripUpdate.StopTimeUpdate> fetchTransitSchedule() throws TransitClientException {
        URL url;
        try {
            System.out.println("---fetch new time with api---");
            url = new URL(MTA_TRANSIT_URL);
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




}
