package com.gerrnbutton.service;

import com.gerrnbutton.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        com.gerrnbutton.entity.User appUser = userRepository.findByUsername(username);

        if (appUser == null) {
            throw new UsernameNotFoundException(username + " not found");
        }
        UserDetails userDetails = User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .roles("USER").build();
        return userDetails;
    }

}