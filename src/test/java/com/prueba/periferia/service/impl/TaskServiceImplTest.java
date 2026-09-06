package com.prueba.periferia.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.prueba.periferia.dto.CreateTaskRequest;
import com.prueba.periferia.dto.PageResponse;
import com.prueba.periferia.dto.TaskResponse;
import com.prueba.periferia.dto.UpdateTaskRequest;
import com.prueba.periferia.exception.ResourceNotFoundException;
import com.prueba.periferia.model.Task;
import com.prueba.periferia.model.TaskStatus;
import com.prueba.periferia.model.User;
import com.prueba.periferia.repository.TaskRepository;
import com.prueba.periferia.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

	@Mock
	private TaskRepository taskRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private TaskServiceImpl taskService;

	private User user;
	private Task task;
	private Pageable pageable;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(1L);
		user.setName("John");
		user.setEmail("john@example.com");

		task = new Task();
		task.setId(10L);
		task.setTitle("Task 1");
		task.setDescription("Desc");
		task.setCreatedAt(LocalDateTime.now());
		task.setDueDate(LocalDateTime.now().plusDays(1));
		task.setStatus(TaskStatus.PENDING);
		task.setUser(user);

		pageable = PageRequest.of(0, 10);
	}

	@Test
	void create_shouldSaveTask_whenUserExists() {
		CreateTaskRequest request = new CreateTaskRequest();
		request.setTitle("Task 1");
		request.setDescription("Desc");
		request.setDueDate(LocalDateTime.now().plusDays(1));
		request.setUserId(1L);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
			Task saved = invocation.getArgument(0);
			saved.setId(10L);
			saved.setCreatedAt(LocalDateTime.now());
			return saved;
		});

		TaskResponse response = taskService.create(request);

		assertEquals(10L, response.getId());
		assertEquals("Task 1", response.getTitle());
		assertEquals(TaskStatus.PENDING, response.getStatus());
		assertEquals(1L, response.getUserId());
	}

	@Test
	void create_shouldThrow_whenUserNotFound() {
		CreateTaskRequest request = new CreateTaskRequest();
		request.setTitle("Task 1");
		request.setUserId(99L);

		when(userRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> taskService.create(request));
		verify(taskRepository, never()).save(any());
	}

	@Test
	void findByUserId_shouldReturnAllTasks_whenStatusIsNull() {
		when(userRepository.existsById(1L)).thenReturn(true);
		when(taskRepository.findByUserId(eq(1L), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(task), pageable, 1));

		PageResponse<TaskResponse> response = taskService.findByUserId(1L, null, pageable);

		assertEquals(1, response.getContent().size());
		assertEquals(10L, response.getContent().get(0).getId());
		assertEquals(0, response.getPage());
		assertTrue(response.isLast());
		verify(taskRepository).findByUserId(1L, pageable);
		verify(taskRepository, never()).findByUserIdAndStatus(any(), any(), any());
	}

	@Test
	void findByUserId_shouldFilterByStatus_whenStatusProvided() {
		when(userRepository.existsById(1L)).thenReturn(true);
		when(taskRepository.findByUserIdAndStatus(eq(1L), eq(TaskStatus.PENDING), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(task), pageable, 1));

		PageResponse<TaskResponse> response = taskService.findByUserId(1L, TaskStatus.PENDING, pageable);

		assertEquals(1, response.getContent().size());
		verify(taskRepository).findByUserIdAndStatus(1L, TaskStatus.PENDING, pageable);
	}

	@Test
	void findByUserId_shouldThrow_whenUserNotFound() {
		when(userRepository.existsById(99L)).thenReturn(false);

		assertThrows(ResourceNotFoundException.class,
				() -> taskService.findByUserId(99L, null, pageable));
	}

	@Test
	void update_shouldUpdateTask_whenExists() {
		UpdateTaskRequest request = new UpdateTaskRequest();
		request.setTitle("Updated");
		request.setDescription("New desc");
		request.setDueDate(LocalDateTime.now().plusDays(2));
		request.setStatus(TaskStatus.COMPLETED);

		when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
		when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

		TaskResponse response = taskService.update(10L, request);

		assertEquals("Updated", response.getTitle());
		assertEquals(TaskStatus.COMPLETED, response.getStatus());
	}

	@Test
	void update_shouldThrow_whenTaskNotFound() {
		UpdateTaskRequest request = new UpdateTaskRequest();
		request.setTitle("Updated");
		request.setStatus(TaskStatus.COMPLETED);

		when(taskRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> taskService.update(99L, request));
	}

	@Test
	void delete_shouldRemoveTask_whenExists() {
		when(taskRepository.existsById(10L)).thenReturn(true);

		taskService.delete(10L);

		verify(taskRepository).deleteById(10L);
	}

	@Test
	void delete_shouldThrow_whenTaskNotFound() {
		when(taskRepository.existsById(99L)).thenReturn(false);

		assertThrows(ResourceNotFoundException.class, () -> taskService.delete(99L));
		verify(taskRepository, never()).deleteById(any());
	}

}
