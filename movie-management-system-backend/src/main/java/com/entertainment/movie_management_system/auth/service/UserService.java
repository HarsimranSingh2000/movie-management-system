package com.entertainment.movie_management_system.auth.service;

import com.entertainment.movie_management_system.auth.dto.SigninRequest;
import com.entertainment.movie_management_system.auth.dto.SigninResponse;
import com.entertainment.movie_management_system.auth.dto.SignupRequest;

public interface UserService {
    void createUser (SignupRequest request);
    SigninResponse validateUserAndSignin (SigninRequest request);
}
