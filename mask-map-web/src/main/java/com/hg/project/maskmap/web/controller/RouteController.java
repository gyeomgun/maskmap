package com.hg.project.maskmap.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class RouteController {

    @RequestMapping(value={"/"}, method = RequestMethod.GET)
    public String index() {
        return "index";
    }
    @RequestMapping("/{path:[^\\.]*}")
    public String redirct() {
        return "forward:/";
    }
}
