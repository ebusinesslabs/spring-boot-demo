package com.example.sbdemo.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Controller
public class NewsController {

    private static final int SLIDING_SIZE = 5;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/news/";

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
        model.addAttribute("news", optionalNews.get());
        return "views/news-update";

    }

    @PostMapping(value = "/news/{id:[\\d]+}")
    public String updateNews(@Valid @ModelAttribute News news,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             @RequestParam("pic_file") MultipartFile file) {
        if (bindingResult.hasErrors()) {
            return "views/news-update";
        }

        if (!file.isEmpty()) {
            news.setPicture(this.uploadFile(file));
        }
        this.newsRepository.save(news);
        redirectAttributes.addFlashAttribute("message", String.format("Record %d saved successfully.", news.getId()));
        return "redirect:/news";
    }

    @GetMapping("/news")
    public String showListNews(
            Model model,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<News> news = this.newsRepository.findAll(pageable);

        int minpage = Math.max(pageable.getPageNumber() - SLIDING_SIZE, 0);
        int maxpage = Math.min(minpage + 2 * SLIDING_SIZE, news.getTotalPages() - 1);
        model.addAttribute("minpage", minpage);
        model.addAttribute("maxpage", maxpage);
        model.addAttribute("news", news);
        return "views/news-list";
    }

    @GetMapping("/news/{id:[\\d]+}/delete")
    public String deleteNews(@PathVariable("id") Long id) {
        this.newsRepository.deleteById(id);
        return "redirect:/news";
    }

    private String uploadFile(MultipartFile file) {
            UUID uuid = UUID.randomUUID();
            try {
                byte[] bytes = file.getBytes();
                String filename = file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + uuid.toString() + filename.substring(filename.lastIndexOf(".") + 1));
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
                return "views/news-update";
            }
        return uuid.toString();
    }
}
