package com.example.thdemo.controller;

import com.example.thdemo.MessagePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HomePageController {

    private MessagePrinter messagePrinter;

    private static final Map<String,String> TEMPLATE;
    static {
        TEMPLATE  = new HashMap<>();
        TEMPLATE.put("VIEW", "home");
        TEMPLATE.put("TITLE", "Home Page");
    }

    @Autowired
    public HomePageController(MessagePrinter messagePrinter) {

        this.messagePrinter = messagePrinter;
    }

    @GetMapping("/home")
    public String index(Model model) {
        model.addAttribute("TEMPLATE", TEMPLATE);
        model.addAttribute("hello", this.messagePrinter.sayHello());
        return "default";
    }
}
