package com.example.thdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {

    @GetMapping("/home")
    public String index(Model model) {
        model.addAttribute("hello", "Hello");
        return "home";
    }
}
