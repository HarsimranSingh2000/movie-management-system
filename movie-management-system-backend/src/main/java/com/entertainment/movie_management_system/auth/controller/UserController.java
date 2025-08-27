package com.entertainment.movie_management_system.auth.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entertainment.movie_management_system.auth.dto.SignupRequest;
import com.entertainment.movie_management_system.auth.exception.ErrorMapper;
import com.entertainment.movie_management_system.auth.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final ErrorMapper errorMapper;

    public UserController(UserService userService, ErrorMapper errorMapper) {
        this.userService = userService;
        this.errorMapper = errorMapper;
    }

    @PostMapping("/signup")
    ResponseEntity<Object> signup(@Valid @RequestBody SignupRequest request) {
        try {
            this.userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + this.errorMapper.createErrorMap(e).replace("\"", "\\\"") + "\"}");
        }
    }
}
