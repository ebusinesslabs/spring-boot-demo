package com.example.sbdemo.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SysConfigController {

    @GetMapping("/sysconfig")
    public String sysConfig(Model model) {
        return "sbadmin";
    }
}
