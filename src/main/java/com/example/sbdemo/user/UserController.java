package com.example.sbdemo.user;

import com.example.sbdemo.auth.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Optional;

@Controller
public class UserController {

    private static final String[] ROLES = {"editor", "contributor", "user"};
    private static final int SLIDING_SIZE = 5;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @InitBinder
    protected void initBinder(final WebDataBinder binder) {
        binder.addValidators(userValidator);
    }

    @GetMapping("/users")
    public String showListUsers(Model model, @PageableDefault(size = 5) Pageable pageable) {
        Page<User> users = this.userRepository.findAll(pageable);
        int minpage = Math.max(pageable.getPageNumber() - SLIDING_SIZE, 0);
        int maxpage = Math.min(minpage + 2 * SLIDING_SIZE, users.getTotalPages() - 1);

        model.addAttribute("minpage", minpage);
        model.addAttribute("maxpage", maxpage);
        model.addAttribute("users", users);
        return "views/user-list";
    }

    @GetMapping("/user/{id:[\\d]+}")
    public String showUpdatePage(Model model, @PathVariable("id") Long id) {
        Optional<User> optionalUser = this.userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException();
        }
        model.addAttribute("user", optionalUser.get());
        model.addAttribute("allroles", ROLES);
        return "views/user-update";
    }

    @PostMapping(value = "/user")
    public String updateUser(@Valid @ModelAttribute User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "views/user-update";
        }

        User dbUser  = userRepository.findById(user.getId()).get();
        dbUser.setFname(user.getFname());
        dbUser.setLname(user.getLname());
        dbUser.setEmail(user.getEmail());

        // create json array from string e.g. "user, editor"
        // convert json array to string e.g ["user", "editor"]
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        for (String role : user.getRoles().split(",")) {
            arrayNode.add(role);
        }
        dbUser.setRoles(arrayNode.toString());

        if (!user.getPassword().isEmpty()) {
            dbUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        userRepository.save(dbUser);

        return "redirect:/users";
    }
}
