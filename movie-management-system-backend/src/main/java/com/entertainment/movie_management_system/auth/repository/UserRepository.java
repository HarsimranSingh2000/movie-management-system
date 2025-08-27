package com.entertainment.movie_management_system.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entertainment.movie_management_system.auth.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
}
