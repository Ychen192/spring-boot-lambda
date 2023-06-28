package com.yan.springbootlambda.model;

import lombok.Data;

import java.util.List;

@Data
public class TransitTime {

    private List<Long> north;
    private List<Long> south;

    public TransitTime(List<Long> north, List<Long> south) {
        this.north = north;
        this.south = south;
    }
}
