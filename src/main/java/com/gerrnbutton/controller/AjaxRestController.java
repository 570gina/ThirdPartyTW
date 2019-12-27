package com.gerrnbutton.controller;

import com.gerrnbutton.service.AuthorizationService;
import com.gerrnbutton.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class AjaxRestController {

    @Autowired
    UserService userService;
    @Autowired
    AuthorizationService authorizationService;

    @GetMapping("/checkAuthState")
    public Boolean checkAuthState(Principal principal){
        return authorizationService.checkAuthState(userService.searchID(principal.getName()));
    }
}
