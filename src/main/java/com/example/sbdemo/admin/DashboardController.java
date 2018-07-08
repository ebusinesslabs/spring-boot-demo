package com.example.sbdemo.admin;

import com.example.sbdemo.news.NewsRepository;
import com.example.sbdemo.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserRepository userRepository;

    private final NewsRepository newsRepository;

    private static Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    public DashboardController(UserRepository userRepository, NewsRepository newsRepository) {
        this.userRepository = userRepository;
        this.newsRepository = newsRepository;
    }

    @GetMapping("/dashboard")
    public String index(Model model) {
        //SecurityContext context = SecurityContextHolder.getContext();
        //Authentication authentication = context.getAuthentication();
        //logger.warn(authentication.getPrincipal().toString());
        model.addAttribute("countusers", userRepository.count());
        model.addAttribute("countnews", newsRepository.count());
        return "views/dashboard";
    }

}
