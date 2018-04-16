package com.example.sbdemo.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class NewsAddController {

    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    private NewsRepository newsRepository;

    @Autowired
    public NewsAddController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @GetMapping(value="/news/add")
    public String showForm(Model model) {
        model.addAttribute("news", new News());
        return "views/news-add";
    }

    @PostMapping(value="/news/add")
    public String addNews(@ModelAttribute News news, @RequestParam("pic_file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
                Files.write(path, bytes);
                news.setPicture(file.getOriginalFilename());
            } catch (IOException e) {
                e.printStackTrace();
                return "views/news-add";
            }
        }
        this.newsRepository.save(news);
        return "redirect:/news";
    }
}
