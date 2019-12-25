package com.gerrnbutton.controller;

import com.gerrnbutton.model.User;
import com.gerrnbutton.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class WebController {
    @Autowired
    UserService userService;

    @GetMapping("/")
    public String index() {
       return "index";
    }
    @GetMapping("login")
    public String login_page() {
        return "login";
    }
    @GetMapping("register")
    public String register_page() {
        return "register";
    }

    @PostMapping("perform_register")
    public String perform_register(User user, Map<String,Object> map){
        userService.insertUser(user);
        return "redirect:/login?registered";
    }
}
