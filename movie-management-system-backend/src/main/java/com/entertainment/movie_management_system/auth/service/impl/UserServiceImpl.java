package com.entertainment.movie_management_system.auth.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.entertainment.movie_management_system.auth.dto.ValidateOtpRequest;
import com.entertainment.movie_management_system.auth.dto.GenerateOtpRequest;
import com.entertainment.movie_management_system.auth.dto.PasswordResetRequest;
import com.entertainment.movie_management_system.auth.dto.SigninRequest;
import com.entertainment.movie_management_system.auth.dto.SigninResponse;
import com.entertainment.movie_management_system.auth.dto.SignupRequest;
import com.entertainment.movie_management_system.auth.entity.OtpTable;
import com.entertainment.movie_management_system.auth.entity.User;
import com.entertainment.movie_management_system.auth.repository.OtpRepository;
import com.entertainment.movie_management_system.auth.repository.UserRepository;
import com.entertainment.movie_management_system.auth.service.UserService;
import com.entertainment.movie_management_system.config.JwtConfig;

import io.jsonwebtoken.Claims;

@Service
public class UserServiceImpl implements UserService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,JwtConfig jwtService, OtpRepository otpRepository) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public void createUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new RuntimeException("Mobile number is already registered");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setGender(request.getGender());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Secure hash
        user.setUserRole("U"); // Enforce default here

        userRepository.save(user);
    }

    @Override
    public SigninResponse validateUserAndSignin(SigninRequest request) {
        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new BadCredentialsException("User does not exist");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        Map<String, Object> claims = new HashMap<>();

        claims.put("type", "password");  
        claims.put("purpose", "session_login");

        String token = jwtService.generateToken(request.getEmail(),claims, Duration.ofMinutes(20));
        return new SigninResponse(token, user.getFirstName(), user.getUserRole());
    }

    @Override
    public void generateOtp(GenerateOtpRequest request){
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new BadCredentialsException("User does not exist");
        }
        //user is in the system
        OtpTable existingOtp = otpRepository.findByEmail(user.getEmail());
        // Check if OTP row exists for this user
        String otp;
        LocalDateTime now = LocalDateTime.now();
        if (existingOtp == null) {
            // No OTP row exists, generate new OTP and store
            otp = String.valueOf((int)(Math.random() * 900000) + 100000); // 6-digit OTP
            OtpTable newOtp = new OtpTable();
            newOtp.setEmail(user.getEmail());
            newOtp.setOtp(otp); //testing only
            //newOtp.setOtp(passwordEncoder.encode(otp));
            newOtp.setOtpUsed(false);
            newOtp.setGeneratedOn(now);
            otpRepository.save(newOtp);
            
            // In real app, send OTP via email/SMS here
            
        } else {
            // OTP row exists, check time difference
            LocalDateTime generatedOn = existingOtp.getGeneratedOn();
            long secondsSinceLastOtp = java.time.Duration.between(generatedOn, now).getSeconds();
            if (secondsSinceLastOtp < 60) {
                // Not enough time has passed, ask user to wait
                int waitSeconds = (int)(60 - secondsSinceLastOtp);
                throw new RuntimeException("Please wait " + waitSeconds + " seconds before requesting a new OTP.");
            } else {
                // Enough time has passed, generate new OTP and update row
                otp = String.valueOf((int)(Math.random() * 900000) + 100000); // 6-digit OTP
                existingOtp.setOtp(otp); //testing only
                //existingOtp.setOtp(passwordEncoder.encode(otp));
                existingOtp.setOtpUsed(false);
                existingOtp.setGeneratedOn(now);
                otpRepository.save(existingOtp);
                // In real app, send OTP via email/SMS here
            }
        }
    }

    @Override
    public void validateOtp (ValidateOtpRequest request){

        OtpTable otpData = otpRepository.findByEmail(request.getEmail());

        if (otpData == null){
            throw new RuntimeException("Otp not generated. please retry.");
        }

        if (otpData.isOtpUsed() && !jwtService.validateToken(otpData.getOtpToken())){
            throw new RuntimeException("Otp Expired, Please regenerate Otp.");
        }

        if (!request.getOtp().equalsIgnoreCase(otpData.getOtp())){
        //if (!passwordEncoder.matches(request.getOtp(), otpData.getOtp())){
            throw new BadCredentialsException("Invalid OTP. Please enter correct otp.");
        }

        Map<String, Object> claims = new HashMap<>();

        claims.put("type", "otp");  
        claims.put("purpose", "reset_password");

        String token = jwtService.generateToken(request.getEmail(),claims, Duration.ofMinutes(5));

        otpData.setOtpToken(token);
        otpRepository.save(otpData);
    }

    @Override
    public void resetPassword (PasswordResetRequest request){
        OtpTable otpData = otpRepository.findByEmail(request.getEmail());

        if (otpData == null){
            throw new BadCredentialsException("User does not exist.");
        }
        String otpToken = otpData.getOtpToken();
        boolean isOtpUsed = otpData.isOtpUsed();
        if (isOtpUsed && !jwtService.validateToken(otpToken)) {
            throw new BadCredentialsException("Invalid or expired OTP. Please generate a new one.");
        }
        
        Claims claims = jwtService.extractAllClaims(otpToken);

        if (!"otp".equals(claims.get("type")) || 
            !"reset_password".equals(claims.get("purpose"))) {
            throw new RuntimeException("Unauthorized password reset attempt");
        }

        // ✅ Token is valid → Reset password
        User user = userRepository.findByEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpData.setOtpUsed(true);
        otpRepository.save(otpData);
    }
}
