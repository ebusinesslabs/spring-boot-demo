package com.example.sbdemo.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm(Model model, String error) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "views/login";
    }
}
