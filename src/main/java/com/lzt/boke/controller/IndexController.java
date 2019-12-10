package com.lzt.boke.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class IndexController {

    //@RequestMapping(value = "/he", method = RequestMethod.GET)
    @GetMapping(value = "/")
    public String hello() {
        return "index";
    }
}
