package com.prueba.periferia.dto;

import java.time.LocalDateTime;

import com.prueba.periferia.model.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateTaskRequest {

	@NotBlank
	private String title;

	private String description;

	private LocalDateTime dueDate;

	@NotNull
	private TaskStatus status;

}
