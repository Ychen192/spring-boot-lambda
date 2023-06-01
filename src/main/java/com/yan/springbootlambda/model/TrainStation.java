package com.yan.springbootlambda.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TrainStation {
    String stationId;
    String stationName;

    public TrainStation(String stationId, String stationName) {
        this.stationId = stationId;
        this.stationName = stationName;
    }
}
