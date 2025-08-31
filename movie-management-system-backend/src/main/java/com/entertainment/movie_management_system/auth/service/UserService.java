package com.entertainment.movie_management_system.auth.service;


import com.entertainment.movie_management_system.auth.dto.ValidateOtpRequest;
import com.entertainment.movie_management_system.auth.dto.GenerateOtpRequest;
import com.entertainment.movie_management_system.auth.dto.PasswordResetRequest;
import com.entertainment.movie_management_system.auth.dto.SigninRequest;
import com.entertainment.movie_management_system.auth.dto.SigninResponse;
import com.entertainment.movie_management_system.auth.dto.SignupRequest;

public interface UserService {
    void createUser (SignupRequest request);
    SigninResponse validateUserAndSignin (SigninRequest request);
    void generateOtp (GenerateOtpRequest request);
    void validateOtp (ValidateOtpRequest request);
    void resetPassword (PasswordResetRequest request);

}
