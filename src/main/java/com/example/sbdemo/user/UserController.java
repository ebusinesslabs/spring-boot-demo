package com.example.sbdemo.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
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

    @PostMapping(value = "/user")
    public String updateUser(@ModelAttribute User user, BindingResult bindingResult) {
        logger.info(bindingResult.getModel().toString());

        if (bindingResult.hasErrors()) {
            return "views/user-update";
        }
//        User dbUser  = userRepository.findById(user.getId()).get();
//        dbUser.setFname(user.getFname());
//        dbUser.setLname(user.getLname());
//        dbUser.setEmail(user.getEmail());
        userRepository.save(user);
        return "redirect:/users";
    }
}
