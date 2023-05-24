package com.yan.springbootlambda.model;

import lombok.Data;

@Data
public class Response {
    boolean success;
    String response;

    public Response(boolean success, String response) {
        this.success = success;
        this.response = response;
    }
}
