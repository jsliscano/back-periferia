package com.prueba.periferia.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.prueba.periferia.dto.CreateTaskRequest;
import com.prueba.periferia.dto.PageResponse;
import com.prueba.periferia.dto.TaskResponse;
import com.prueba.periferia.dto.UpdateTaskRequest;
import com.prueba.periferia.model.TaskStatus;
import com.prueba.periferia.service.ITaskService;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

	@Mock
	private ITaskService taskService;

	@InjectMocks
	private TaskController taskController;

	@Test
	void create_shouldReturnCreated() {
		CreateTaskRequest request = new CreateTaskRequest();
		request.setTitle("Task");
		request.setUserId(1L);

		TaskResponse body = new TaskResponse(1L, "Task", null, LocalDateTime.now(), null, TaskStatus.PENDING, 1L);
		when(taskService.create(request)).thenReturn(body);

		ResponseEntity<TaskResponse> response = taskController.create(request);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(1L, response.getBody().getId());
	}

	@Test
	void findByUserId_shouldReturnPage() {
		PageResponse<TaskResponse> page = new PageResponse<>(List.of(), 0, 10, 0, 0, true);
		when(taskService.findByUserId(eq(1L), eq(TaskStatus.PENDING), any(Pageable.class))).thenReturn(page);

		ResponseEntity<PageResponse<TaskResponse>> response =
				taskController.findByUserId(1L, TaskStatus.PENDING, 0, 10);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(page, response.getBody());
	}

	@Test
	void update_shouldReturnOk() {
		UpdateTaskRequest request = new UpdateTaskRequest();
		request.setTitle("Updated");
		request.setStatus(TaskStatus.COMPLETED);

		TaskResponse body = new TaskResponse(1L, "Updated", null, LocalDateTime.now(), null, TaskStatus.COMPLETED, 1L);
		when(taskService.update(1L, request)).thenReturn(body);

		ResponseEntity<TaskResponse> response = taskController.update(1L, request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Updated", response.getBody().getTitle());
	}

	@Test
	void delete_shouldReturnNoContent() {
		ResponseEntity<Void> response = taskController.delete(1L);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(taskService).delete(1L);
	}

}
