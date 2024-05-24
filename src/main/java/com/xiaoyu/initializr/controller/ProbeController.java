package com.xiaoyu.initializr.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProbeController {

    @GetMapping("probe")
    public String liveness() {
        return "success";
    }
}
