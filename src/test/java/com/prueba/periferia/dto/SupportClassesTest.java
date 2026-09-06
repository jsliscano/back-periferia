package com.prueba.periferia.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.prueba.periferia.exception.EmailAlreadyExistsException;
import com.prueba.periferia.exception.InvalidCredentialsException;
import com.prueba.periferia.exception.ResourceNotFoundException;
import com.prueba.periferia.model.Task;
import com.prueba.periferia.model.TaskStatus;

class SupportClassesTest {

	@Test
	void pageResponse_from_shouldMapPageFields() {
		PageResponse<String> response = PageResponse.from(
				new PageImpl<>(List.of("a", "b"), PageRequest.of(1, 2), 5));

		assertEquals(List.of("a", "b"), response.getContent());
		assertEquals(1, response.getPage());
		assertEquals(2, response.getSize());
		assertEquals(5, response.getTotalElements());
		assertEquals(3, response.getTotalPages());
		assertTrue(!response.isLast() || response.getTotalPages() > 0);
	}

	@Test
	void exceptions_shouldExposeMessages() {
		assertTrue(new EmailAlreadyExistsException("a@b.com").getMessage().contains("a@b.com"));
		assertEquals("Invalid email or password", new InvalidCredentialsException().getMessage());
		assertEquals("missing", new ResourceNotFoundException("missing").getMessage());
	}

	@Test
	void task_onCreate_shouldSetDefaults() {
		Task task = new Task();
		task.setStatus(null);

		ReflectionTestUtils.invokeMethod(task, "onCreate");

		assertEquals(TaskStatus.PENDING, task.getStatus());
		assertTrue(task.getCreatedAt() != null);
	}

	@Test
	void task_onCreate_shouldKeepExistingValues() {
		Task task = new Task();
		task.setCreatedAt(java.time.LocalDateTime.of(2020, 1, 1, 0, 0));
		task.setStatus(TaskStatus.COMPLETED);

		ReflectionTestUtils.invokeMethod(task, "onCreate");

		assertEquals(TaskStatus.COMPLETED, task.getStatus());
		assertEquals(2020, task.getCreatedAt().getYear());
	}

	@Test
	void dtoNoArgsConstructors_shouldWork() {
		assertTrue(new RegisterUserRequest() != null);
		assertTrue(new LoginRequest() != null);
		assertTrue(new CreateTaskRequest() != null);
		assertTrue(new UpdateTaskRequest() != null);
		assertTrue(new UserResponse() != null);
		assertTrue(new TaskResponse() != null);
		assertTrue(new LoginResponse() != null);
		assertTrue(new PageResponse<>() != null);
	}

}
