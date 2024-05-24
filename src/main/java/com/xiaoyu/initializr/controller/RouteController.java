package com.xiaoyu.initializr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class RouteController {

    @GetMapping("start")
    public String appInfoPage() {
        return "main";
    }

}
