package com.gerrnbutton.controller;

import com.gerrnbutton.service.AuthorizationService;
import com.gerrnbutton.service.CMDService;
import com.gerrnbutton.service.DataProcessingService;
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
    @Autowired
    DataProcessingService dataProcessingService;

    @GetMapping("/checkAuthState")
    public Boolean checkAuthState(Principal principal){
        if(authorizationService.searchAuthByUser(userService.searchID(principal.getName())) == null) return false;
        else return true;
    }

    @GetMapping("/connectMyData")
    public String connectMyData(Principal principal){
        String data = cmdService.getEnergyData(authorizationService.searchAuthByUser(userService.searchID(principal.getName())), 0);
        return dataProcessingService.organizeDataByEachMonth(data);
    }

    @GetMapping("/updateMyData")
    public String updateMyData(Principal principal) {
        String data = cmdService.getEnergyData(authorizationService.searchAuthByUser(userService.searchID(principal.getName())), 1);
        return dataProcessingService.organizeDataByEachMonth(data);
    }

    @GetMapping("/getSelectedMonthData")
    public String getSelectedMonthData(String year, String month,  Principal principal) {
        String data = cmdService.getEnergyData(authorizationService.searchAuthByUser(userService.searchID(principal.getName())), 0);
        return dataProcessingService.organizeDataByEachDay(data, year, month);
    }

}

