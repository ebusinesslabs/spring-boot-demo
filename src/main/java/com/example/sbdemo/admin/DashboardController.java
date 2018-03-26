package com.example.sbdemo.admin;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

public class DashboardController {

    private static final Map<String,String> TEMPLATE;
    static {
        TEMPLATE  = new HashMap<>();
        TEMPLATE.put("VIEW", "home");
        TEMPLATE.put("TITLE", "Dashboard");
    }

    @GetMapping("/dashboard")
    public String index(Model model) {
        model.addAttribute("TEMPLATE", TEMPLATE);
        return "sbadmin";
    }

}
