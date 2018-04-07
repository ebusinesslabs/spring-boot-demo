package com.example.sbdemo.admin;

import com.example.sbdemo.user.User;
import com.example.sbdemo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String index(Model model) {
        model.addAttribute("parameter", "this is a parameter");
        return "views/dashboard";
    }

}
