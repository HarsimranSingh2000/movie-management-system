package com.entertainment.movie_management_system.auth.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entertainment.movie_management_system.auth.dto.SigninRequest;
import com.entertainment.movie_management_system.auth.dto.SignupRequest;
import com.entertainment.movie_management_system.auth.service.UserService;
import com.entertainment.movie_management_system.exception.ErrorMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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
    @Operation(summary = "Create a new user", description = "Return 201 if the user was successfully created.")
    @ApiResponse(responseCode = "201", description = "User created successfully.")
    @ApiResponse(responseCode = "400")
    ResponseEntity<Object> signup(@Valid @RequestBody SignupRequest request) {
        try {
            this.userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.errorMapper.createErrorMap(e));
        }
    }

    @PostMapping("/signin")
    @Operation(summary = "Login into the application", description = "Returns 200 if user is logged in successfully")
    @ApiResponse(responseCode = "200", description = "User created successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid Credentials.")
    ResponseEntity <Object> signin(@Valid @RequestBody SigninRequest request){
        try {
            return ResponseEntity.ok(this.userService.validateUserAndSignin(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.errorMapper.createErrorMap(e));
        }
    }
}
