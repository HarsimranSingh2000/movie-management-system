package com.entertainment.movie_management_system.auth.service.impl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.entertainment.movie_management_system.auth.dto.SigninRequest;
import com.entertainment.movie_management_system.auth.dto.SigninResponse;
import com.entertainment.movie_management_system.auth.dto.SignupRequest;
import com.entertainment.movie_management_system.auth.entity.User;
import com.entertainment.movie_management_system.auth.repository.UserRepository;
import com.entertainment.movie_management_system.auth.service.UserService;
import com.entertainment.movie_management_system.config.JwtConfig;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,JwtConfig jwtService) {
        this.userRepository = userRepository;
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
        String token = jwtService.generateToken(user.getEmail(), user.getUserRole());

        return new SigninResponse(token, user.getFirstName(), user.getUserRole());
    }
    
}
