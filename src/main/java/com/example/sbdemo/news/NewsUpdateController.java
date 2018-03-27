package com.example.sbdemo.news;

import com.oracle.tools.packager.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;

@Controller
public class NewsUpdateController {

    private NewsRepository newsRepository;

    private static final Map<String,String> TEMPLATE;
    static {
        TEMPLATE  = new HashMap<>();
        TEMPLATE.put("VIEW", "news-update");
        TEMPLATE.put("TITLE", "News Update Page");
    }

    @Autowired
    public NewsUpdateController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @GetMapping(value = "/news/{id:[\\d]+}")
    public String showSingleNews(Model model, @PathVariable("id") Long id) {
        News news = this.newsRepository.findOne(id);
        model.addAttribute("TEMPLATE", TEMPLATE);
        model.addAttribute("news", news);
        return "sbadmin";
    }

    @PostMapping(value = "/news/{id:[\\d]+}")
    public String updateNews(@ModelAttribute News news, RedirectAttributes redirectAttributes) {
        this.newsRepository.save(news);
        redirectAttributes.addFlashAttribute("message", "Record saved successfully.");
        return "redirect:/news";
    }
}
