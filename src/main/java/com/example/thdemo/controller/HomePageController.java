package com.example.thdemo.controller;

import com.example.thdemo.MessagePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {

    private MessagePrinter messagePrinter;

    @Autowired
    public HomePageController(MessagePrinter messagePrinter) {
        this.messagePrinter = messagePrinter;
    }

    @GetMapping("/home")
    public String index(Model model) {
        model.addAttribute("content", "home");
        model.addAttribute("hello", this.messagePrinter.sayHello());
        return "default";
    }
}
