package com.yan.springbootlambda.controller;

import com.yan.springbootlambda.model.Response;
import com.yan.springbootlambda.service.TransitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransitTimeController {

    @Autowired
    private TransitService transitService;

    @RequestMapping(value = "/transit", method = RequestMethod.GET)
    @ResponseBody
    public Response transitTime() {
        String resp = transitService.fetchTransitTime();
        return new Response(true, resp);
    }
}
