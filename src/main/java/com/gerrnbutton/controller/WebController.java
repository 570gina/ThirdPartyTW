package com.gerrnbutton.controller;

import com.gerrnbutton.entity.User;
import com.gerrnbutton.service.AuthorizationService;
import com.gerrnbutton.service.CMDService;
import com.gerrnbutton.service.UserService;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

@Controller
public class WebController {
    @Autowired
    UserService userService;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    CMDService cmdService;

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

    @GetMapping("/redirect")
    public String redirect(String code, Principal principal) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        if (code != null && !code.isEmpty()) {
            authorizationService.auth(code, principal); }
        return "index";
    }

    @PostMapping("perform_register")
    public String perform_register(User user){
        userService.insertUser(user);
        return "redirect:/login?registered";
    }

}
