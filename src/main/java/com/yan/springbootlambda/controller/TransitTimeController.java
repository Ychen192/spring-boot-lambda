package com.yan.springbootlambda.controller;

import com.yan.springbootlambda.exception.TransitClientException;
import com.yan.springbootlambda.service.TransitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransitTimeController {

    TransitService transitService;

    @Autowired
    public TransitTimeController(TransitService transitService) {
        this.transitService = transitService;
    }

    @RequestMapping(value = "/train/time/{stationId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> transitTime(@PathVariable String stationId) throws TransitClientException {
        var resp = transitService.fetchTransitTime(stationId);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @RequestMapping(value = "/stops/{line}", method = RequestMethod.GET)
    public ResponseEntity<Object> stops(@PathVariable Character line) {
        var resp = transitService.fetchTransitStops(line);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
