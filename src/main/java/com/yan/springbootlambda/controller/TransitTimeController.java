package com.yan.springbootlambda.controller;

import com.yan.springbootlambda.exception.TransitClientException;
import com.yan.springbootlambda.model.Response;
import com.yan.springbootlambda.service.TransitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransitTimeController {

    @Autowired
    private TransitService transitService;

    @RequestMapping(value = "/transit", method = RequestMethod.GET)
    @ResponseBody
    public Response transitTime() throws TransitClientException {
        String resp = transitService.fetchTransitTime();
        return new Response(true, resp);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public Response hello() {
        return new Response(true, "Hello World");
    }

    @RequestMapping(value = "/testClientException", method = RequestMethod.GET)
    @ResponseBody
    public Response clientException() throws TransitClientException {
        throw new TransitClientException();
    }

    @RequestMapping(value = "/testException", method = RequestMethod.GET)
    @ResponseBody
    public Response exception() {
        throw new RuntimeException();
    }
}
