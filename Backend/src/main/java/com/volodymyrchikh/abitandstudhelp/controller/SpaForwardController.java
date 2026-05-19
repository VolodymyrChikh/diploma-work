package com.volodymyrchikh.abitandstudhelp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping(value = {
            "/",
            "/signup",
            "/signin",
            "/main",
            "/about-specialties",
            "/course-map",
            "/forum",
            "/forum/post/{slug}",
            "/post/{slug}",
            "/media",
            "/create-post",
            "/profile"
    })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
