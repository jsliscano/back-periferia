package com.prueba.periferia.service;

import com.prueba.periferia.dto.LoginRequest;
import com.prueba.periferia.dto.LoginResponse;
import com.prueba.periferia.dto.RegisterUserRequest;
import com.prueba.periferia.dto.UserResponse;

public interface IUserService {

	UserResponse register(RegisterUserRequest request);

	LoginResponse login(LoginRequest request);

}
