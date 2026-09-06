package com.prueba.periferia.service;

import org.springframework.data.domain.Pageable;

import com.prueba.periferia.dto.CreateTaskRequest;
import com.prueba.periferia.dto.PageResponse;
import com.prueba.periferia.dto.TaskResponse;
import com.prueba.periferia.dto.UpdateTaskRequest;
import com.prueba.periferia.model.TaskStatus;

public interface ITaskService {

	TaskResponse create(CreateTaskRequest request);

	PageResponse<TaskResponse> findByUserId(Long userId, TaskStatus status, Pageable pageable);

	TaskResponse update(Long id, UpdateTaskRequest request);

	void delete(Long id);

}
