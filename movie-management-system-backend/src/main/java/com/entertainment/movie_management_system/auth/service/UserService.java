package com.entertainment.movie_management_system.auth.service;

import com.entertainment.movie_management_system.auth.dto.SignupRequest;

public interface UserService {
    void createUser (SignupRequest request);
}
