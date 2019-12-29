package com.gerrnbutton.controller;

import com.gerrnbutton.service.AuthorizationService;
import com.gerrnbutton.service.CMDService;
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
    @Autowired
    CMDService cmdService;

    @GetMapping("/checkAuthState")
    public Boolean checkAuthState(Principal principal){
        if(authorizationService.searchAuthByUser(userService.searchID(principal.getName())) == null) return false;
        else return true;
    }

    @GetMapping("/getChartData")
    public String getChartData(Principal principal){
        cmdService.getUsagePoints(authorizationService.searchAuthByUser(userService.searchID(principal.getName())));
        return "";
    }
}
