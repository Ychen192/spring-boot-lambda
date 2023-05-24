package com.yan.springbootlambda.controller;

import com.yan.springbootlambda.model.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public Response hello() {
        return new Response(true, "Hello World");
    }
}
