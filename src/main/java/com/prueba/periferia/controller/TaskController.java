package com.prueba.periferia.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prueba.periferia.dto.CreateTaskRequest;
import com.prueba.periferia.dto.PageResponse;
import com.prueba.periferia.dto.TaskResponse;
import com.prueba.periferia.dto.UpdateTaskRequest;
import com.prueba.periferia.model.TaskStatus;
import com.prueba.periferia.service.ITaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

	private final ITaskService taskService;

	@PostMapping
	public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) {
		TaskResponse response = taskService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	@Operation(summary = "List tasks by user", description = "Optional filter by status: PENDING or COMPLETED")
	public ResponseEntity<PageResponse<TaskResponse>> findByUserId(
			@RequestParam Long userId,
			@Parameter(description = "Filter by status. Omit to return all.")
			@RequestParam(required = false) TaskStatus status,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		return ResponseEntity.ok(taskService.findByUserId(userId, status, pageable));
	}

	@PutMapping("/{id}")
	public ResponseEntity<TaskResponse> update(
			@PathVariable Long id,
			@Valid @RequestBody UpdateTaskRequest request) {
		return ResponseEntity.ok(taskService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		taskService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
