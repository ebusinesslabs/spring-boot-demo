package com.example.sbdemo.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class NewsListController {

    private NewsRepository newsRepository;

    @Autowired
    public NewsListController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @GetMapping("/news")
    public String showNews(Model model) {
        model.addAttribute("news", this.newsRepository.findAll());
        return "views/news-list";
    }
}
