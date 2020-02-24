package com.gerrnbutton.service;

import com.gerrnbutton.entity.Role;
import com.gerrnbutton.entity.User;
import com.gerrnbutton.repository.RoleRepository;
import com.gerrnbutton.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void insertUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByRole("USER");
        if(userRole == null){
            userRole = new Role();
            userRole.setRole("USER");
            roleRepository.save(userRole);
            userRole = roleRepository.findByRole("USER");
        }
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        userRepository.save(user);
    };

    public User searchID(String name){
        return userRepository.findByUsername(name);
    };
}
