package com.gerrnbutton.service;

import com.gerrnbutton.model.Authorization;
import com.gerrnbutton.model.User;
import com.gerrnbutton.repository.AuthorizationRepository;
import com.gerrnbutton.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Service
public class AuthorizationService {

    @Autowired
    private AuthorizationRepository authorizationRepository;
    @Autowired
    private UserRepository userRepository;

    public void insertAuthorization(Map<String, Object> map, String username) {
        Authorization authorization = new Authorization();
        authorization.setAccess_token(map.get("access_token").toString());
        authorization.setRefresh_token(map.get("refresh_token").toString());
        String formattedDate = calculateTime((int)map.get("expires_in"));
        authorization.setExpires_in(formattedDate);
        authorization.setToken_type(map.get("token_type").toString());
        User appUser = userRepository.findByUsername(username);
        authorization.setUser(appUser);
        System.out.println(authorization);

        authorizationRepository.save(authorization);
    };

    public Boolean checkAuthState(User user){
        if (authorizationRepository.findByUser(user) == null)
            return false;
        else
            return true;
    }

    public String calculateTime(int datatime){
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, datatime);
        date = c.getTime();
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }
}
