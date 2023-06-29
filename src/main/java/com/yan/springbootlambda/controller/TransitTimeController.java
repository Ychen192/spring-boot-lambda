package com.yan.springbootlambda.controller;

import com.yan.springbootlambda.exception.TransitClientException;
import com.yan.springbootlambda.model.TrainStation;
import com.yan.springbootlambda.service.TransitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransitTimeController {

    TransitService transitService;

    @Autowired
    public TransitTimeController(TransitService transitService) {
        this.transitService = transitService;
    }

    @RequestMapping(value = "/train/time/{trainLine}/{stationId}", method = RequestMethod.GET)
    public ResponseEntity<Object> transitTime(
            @PathVariable Character trainLine,
            @PathVariable String stationId) throws TransitClientException {
        String station = stationId.substring(0, stationId.length() - 1);
        var resp = transitService.fetchTransitTime(trainLine, station);
        return new ResponseEntity<>(resp, responseHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/stops/{line}", method = RequestMethod.GET)
    public ResponseEntity<List<TrainStation>> stops(@PathVariable Character line) throws TransitClientException {
        var resp = transitService.fetchTransitStops(line);
        return new ResponseEntity<>(resp, responseHeaders(), HttpStatus.OK);
    }

    private HttpHeaders responseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        return responseHeaders;
    }
}
