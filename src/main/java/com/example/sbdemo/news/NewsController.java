package com.example.sbdemo.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class NewsController {
    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    private NewsRepository newsRepository;

    @Autowired
    public NewsController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @GetMapping(value="/news/add")
    public String showAddForm(Model model) {
        model.addAttribute("news", new News());
        return "views/news-add";
    }

    @PostMapping(value="/news/add")
    public String addNews(@ModelAttribute News news, @RequestParam("pic_file") MultipartFile file, RedirectAttributes redirectAttributes) {
        news.setPicture(this.uploadFile(file));
        this.newsRepository.save(news);
        return "redirect:/news";
    }

    @GetMapping(value = "/news/{id:[\\d]+}")
    public String showUpdateForm(Model model, @PathVariable("id") Long id) {
        Optional<News> optionalNews = this.newsRepository.findById(id);
        if (!optionalNews.isPresent()) {
            throw new ResourceNotFoundException();
        }
        optionalNews.ifPresent(news -> model.addAttribute("news", optionalNews.get()));
        return "views/news-update";

    }

    @PostMapping(value = "/news/{id:[\\d]+}")
    public String updateNews(@ModelAttribute News news,
                             RedirectAttributes redirectAttributes,
                             @RequestParam("pic_file") MultipartFile file) {

        news.setPicture(this.uploadFile(file));
        this.newsRepository.save(news);
        redirectAttributes.addFlashAttribute("message", "Record saved successfully.");
        return "redirect:/news";
    }

    @GetMapping("/news")
    public String showListNews(Model model) {
        model.addAttribute("news", this.newsRepository.findAll());
        return "views/news-list";
    }

    @GetMapping("/news/{id:[\\d]+}/delete")
    public String deleteNews(@PathVariable("id") Long id) {
        this.newsRepository.deleteById(id);
        return "redirect:/news";
    }

    private String uploadFile(MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
                return "views/news-update";
            }
        }
        return file.getOriginalFilename();
    }
}