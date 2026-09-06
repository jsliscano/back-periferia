package com.prueba.periferia.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prueba.periferia.dto.LoginRequest;
import com.prueba.periferia.dto.LoginResponse;
import com.prueba.periferia.dto.RegisterUserRequest;
import com.prueba.periferia.dto.UserResponse;
import com.prueba.periferia.exception.EmailAlreadyExistsException;
import com.prueba.periferia.exception.InvalidCredentialsException;
import com.prueba.periferia.model.User;
import com.prueba.periferia.repository.UserRepository;
import com.prueba.periferia.security.JwtService;
import com.prueba.periferia.service.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Override
	@Transactional
	public UserResponse register(RegisterUserRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException(request.getEmail());
		}

		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		User saved = userRepository.save(user);

		return new UserResponse(saved.getId(), saved.getName(), saved.getEmail());
	}

	@Override
	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(InvalidCredentialsException::new);

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException();
		}

		String token = jwtService.generateToken(user);
		return new LoginResponse(token, user.getId(), user.getName(), user.getEmail());
	}

}
