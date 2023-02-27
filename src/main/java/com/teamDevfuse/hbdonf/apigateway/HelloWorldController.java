package com.teamDevfuse.hbdonf.apigateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping("test")
    public String Test() {
        return "hell world";
    }
}
