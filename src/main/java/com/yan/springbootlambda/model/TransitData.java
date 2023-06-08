package com.yan.springbootlambda.model;

import com.google.transit.realtime.GtfsRealtime;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class TransitData {
    Instant timeFetched;
    List<GtfsRealtime.TripUpdate.StopTimeUpdate> dataSet;

    public TransitData(Instant timeFetched, List<GtfsRealtime.TripUpdate.StopTimeUpdate> dataSet) {
        this.timeFetched = timeFetched;
        this.dataSet = dataSet;
    }
}
