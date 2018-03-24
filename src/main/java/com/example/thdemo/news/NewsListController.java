package com.example.thdemo.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class NewsListController {

    private NewsRepository newsRepository;

    @Autowired
    public NewsListController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @GetMapping("/news")
    public String showNews(Model model) {
        model.addAttribute("content", "news-list");
        model.addAttribute("news", this.newsRepository.findAll());
        return "default";
    }
}
