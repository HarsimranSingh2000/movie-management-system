package com.entertainment.movie_management_system.auth.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entertainment.movie_management_system.auth.dto.GenerateOtpRequest;
import com.entertainment.movie_management_system.auth.dto.PasswordResetRequest;
import com.entertainment.movie_management_system.auth.dto.ValidateOtpRequest;
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
    @ApiResponse(responseCode = "200", description = "User logged in")
    @ApiResponse(responseCode = "400", description = "Invalid Credentials.")
    ResponseEntity <Object> signin(@Valid @RequestBody SigninRequest request){
        try {
            return ResponseEntity.ok(this.userService.validateUserAndSignin(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.errorMapper.createErrorMap(e));
        }
    }

    @PostMapping("/requestotp")
    @Operation(summary = "generate otp for password reset")
    @ApiResponse(responseCode = "200", description = "otp generated successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid Email/user does not exist")
    ResponseEntity <Object> requestOtp(@Valid @RequestBody GenerateOtpRequest request) {
        try {
                this.userService.generateOtp(request);
                return ResponseEntity.ok("OTP generated successfully. Plese check your email inbox.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.errorMapper.createErrorMap(e));
        }
    }
    
    @PostMapping("/validateotp")
    @Operation(summary = "validate otp for password reset")
    @ApiResponse(responseCode = "200", description = "otp validated successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid otp")
    ResponseEntity <Object> validateOtp(@Valid @RequestBody ValidateOtpRequest request) {
        try {
            this.userService.validateOtp(request);
            return ResponseEntity.ok("OTP validated.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.errorMapper.createErrorMap(e));
        }
    }
    
    
    @PutMapping("/resetpassword")
    @Operation(summary = "Reset password", description = "Returns 200 if password is reset")
    @ApiResponse(responseCode = "200", description = "Password reset successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid Credentials.")
    ResponseEntity <Object> signin(@Valid @RequestBody PasswordResetRequest request){
        try {
            this.userService.resetPassword(request);
            return ResponseEntity.ok("Password reset Successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.errorMapper.createErrorMap(e));
        }
    }
}
