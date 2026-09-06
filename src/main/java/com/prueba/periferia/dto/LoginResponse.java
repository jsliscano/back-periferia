package com.prueba.periferia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

	private String token;
	private String type;
	private Long userId;
	private String name;
	private String email;

	public LoginResponse(String token, Long userId, String name, String email) {
		this.token = token;
		this.type = "Bearer";
		this.userId = userId;
		this.name = name;
		this.email = email;
	}

}
