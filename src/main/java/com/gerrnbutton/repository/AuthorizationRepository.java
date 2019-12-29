package com.gerrnbutton.repository;

import com.gerrnbutton.entity.Authorization;
import com.gerrnbutton.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("authorizationRepository")
public interface AuthorizationRepository extends JpaRepository<Authorization, Long> {
    Authorization findByUser(User user);
}