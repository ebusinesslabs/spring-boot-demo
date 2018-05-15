package com.example.sbdemo.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Controller
public class NewsController {

    private static final int SLIDING_SIZE = 5;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/news/";

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsValidator newsValidator;

    @Autowired
    private MessageSource messageSource;

    @InitBinder
    public void initBinder(final WebDataBinder binder) {
        binder.addValidators(newsValidator);
    }

    @GetMapping(value="/news/add")
    public String showAddForm(Model model) {
        model.addAttribute("news", new News());
        return "views/news-add";
    }

    @PostMapping(value="/news/add")
    public String addNews(@ModelAttribute News news,
                          RedirectAttributes redirectAttributes) {
        try {
            news.setPicture(this.uploadFile(news.getMultipartFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.newsRepository.save(news);
        return "redirect:/news";
    }

    @GetMapping(value = "/news/{id:[\\d]+}")
    public String showUpdateForm(Model model, @PathVariable("id") Long id) {
        Optional<News> optionalNews = this.newsRepository.findById(id);
        if (!optionalNews.isPresent()) {
            throw new ResourceNotFoundException(String.format("record with id %d was not found.", id));
        }
        model.addAttribute("news", optionalNews.get());
        return "views/news-update";

    }

    @PostMapping(value = "/news/{id:[\\d]+}")
    public String updateNews(@Valid @ModelAttribute News news,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) throws IOException {
        if (bindingResult.hasErrors()) {
            return "views/news-update";
        }

        if (!news.getMultipartFile().isEmpty()) {
            news.setPicture(uploadFile(news.getMultipartFile()));
        }

        this.newsRepository.save(news);
        String message = messageSource.getMessage("news.saved.success", new Object[]{news.getId()}, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("message", message);
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

    private String uploadFile(MultipartFile file) throws IOException {
        UUID uuid = UUID.randomUUID();
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        byte[] bytes = file.getBytes();
        Path path = Paths.get(UPLOAD_DIR + uuid.toString() + extension);
        Files.write(path, bytes);

        return uuid.toString() + extension;
    }
}
