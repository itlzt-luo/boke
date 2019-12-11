package com.lzt.boke.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class IndexController {

    //@RequestMapping(value = "/he", method = RequestMethod.GET)
    @GetMapping(value = "/")
    public String hello() {
        return "index";
    }
}
