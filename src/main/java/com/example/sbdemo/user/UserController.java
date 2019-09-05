package com.example.sbdemo.user;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class UserController {

    private static final String[] ROLES = {"administrator", "editor", "user"};
    private static final int SLIDING_SIZE = 5;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    private final UserValidator userValidator;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, UserValidator userValidator, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @InitBinder
    protected void initBinder(final WebDataBinder binder) {
        binder.addValidators(userValidator);
    }

    @GetMapping("/users")
    public String showListUsers(Model model, @PageableDefault(size = 10) Pageable pageable) {
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
        if (optionalUser.isEmpty()) {
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

        dbUser.setRoles(convertRolesToJsonString(user.getRoles()));

        if (!user.getPassword().isEmpty()) {
            dbUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        userRepository.save(dbUser);

        return "redirect:/users";
    }

    @GetMapping("/user/add")
    public String addUserForm(Model model) {
        User user = new User();
        // Thymeleaf exception for roles:
        // Cannot apply contains on null
        user.setRoles("");
        model.addAttribute("user", user);

        model.addAttribute("allroles", ROLES);
        return "views/user-add";
    }

    @PostMapping("/user/add")
    public String addUser(@Valid @ModelAttribute User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "views/user-add";
        }
        user.setRoles(convertRolesToJsonString(user.getRoles()));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/users";
    }

    private String convertRolesToJsonString(String roles) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        for (String role : roles.split(",")) {
            arrayNode.add(role);
        }
        return arrayNode.toString();
    }
}
