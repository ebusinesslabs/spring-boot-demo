package com.example.sbdemo.news;

import com.oracle.tools.packager.Log;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Controller
public class NewsUpdateController {

    private static final String UPLOAD_DIR = "src/main/resources/static/images/";
    private NewsRepository newsRepository;

    @Autowired
    public NewsUpdateController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @GetMapping(value = "/news/{id:[\\d]+}")
    public String showSingleNews(Model model, @PathVariable("id") Long id) {
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
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
                Files.write(path, bytes);
                news.setPicture(file.getOriginalFilename());
            } catch (IOException e) {
                e.printStackTrace();
                return "views/news-update";
            }
        }

        this.newsRepository.save(news);
        redirectAttributes.addFlashAttribute("message", "Record saved successfully.");
        return "redirect:/news";
    }
}
