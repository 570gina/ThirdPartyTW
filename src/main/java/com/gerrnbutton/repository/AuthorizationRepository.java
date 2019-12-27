package com.gerrnbutton.repository;

import com.gerrnbutton.model.Authorization;
import com.gerrnbutton.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("authorizationRepository")
public interface AuthorizationRepository extends JpaRepository<Authorization, Long> {
    Authorization findByUser(User user);
}