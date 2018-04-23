package com.example.sbdemo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public String showListUsers(Model model) {
        model.addAttribute("users", this.userRepository.findAll());
        return "views/user-list";
    }

    @GetMapping("/user/{id:[\\d]+}")
    public String showUpdatePage(Model model, @PathVariable("id") Long id) {
        Optional<User> optionalUser = this.userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException();
        }
        model.addAttribute("user", optionalUser.get());
        return "views/user-update";
    }
}
