package com.prueba.periferia.dto;

import java.time.LocalDateTime;

import com.prueba.periferia.model.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

	private Long id;
	private String title;
	private String description;
	private LocalDateTime createdAt;
	private LocalDateTime dueDate;
	private TaskStatus status;
	private Long userId;

}
