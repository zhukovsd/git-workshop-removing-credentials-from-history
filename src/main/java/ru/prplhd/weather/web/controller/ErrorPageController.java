package ru.prplhd.weather.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {

    @GetMapping("/404")
    public String notFoundPage() {
        return "404";
    }
}
