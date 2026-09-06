package com.prueba.periferia.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.prueba.periferia.service.ITaskService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements ITaskService {

	private final TaskRepository taskRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public TaskResponse create(CreateTaskRequest request) {
		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

		Task task = new Task();
		task.setTitle(request.getTitle());
		task.setDescription(request.getDescription());
		task.setDueDate(request.getDueDate());
		task.setStatus(TaskStatus.PENDING);
		task.setUser(user);

		Task saved = taskRepository.save(task);
		return toResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<TaskResponse> findByUserId(Long userId, TaskStatus status, Pageable pageable) {
		if (!userRepository.existsById(userId)) {
			throw new ResourceNotFoundException("User not found with id: " + userId);
		}

		Page<Task> tasks = (status == null)
				? taskRepository.findByUserId(userId, pageable)
				: taskRepository.findByUserIdAndStatus(userId, status, pageable);

		return PageResponse.from(tasks.map(this::toResponse));
	}

	@Override
	@Transactional
	public TaskResponse update(Long id, UpdateTaskRequest request) {
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

		task.setTitle(request.getTitle());
		task.setDescription(request.getDescription());
		task.setDueDate(request.getDueDate());
		task.setStatus(request.getStatus());

		Task updated = taskRepository.save(task);
		return toResponse(updated);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		if (!taskRepository.existsById(id)) {
			throw new ResourceNotFoundException("Task not found with id: " + id);
		}
		taskRepository.deleteById(id);
	}

	private TaskResponse toResponse(Task task) {
		return new TaskResponse(
				task.getId(),
				task.getTitle(),
				task.getDescription(),
				task.getCreatedAt(),
				task.getDueDate(),
				task.getStatus(),
				task.getUser().getId());
	}

}
