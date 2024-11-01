package com.example.teamcity.requests;

import com.example.teamcity.enums.Endpoint;
import io.restassured.specification.RequestSpecification;

public class Request {
    /**
     * Request - это класс, описывающий меняющиеся параметры запроса, такие как:
     *  спецификация, эндпоинт (relative URL, model)
     */

    protected final RequestSpecification spec;
    protected Endpoint endpoint;

    public Request(RequestSpecification spec, Endpoint endpoint) {
        this.spec = spec;
        this.endpoint = endpoint;
    }

}
