package com.prueba.periferia.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prueba.periferia.model.Task;
import com.prueba.periferia.model.TaskStatus;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	Page<Task> findByUserId(Long userId, Pageable pageable);

	Page<Task> findByUserIdAndStatus(Long userId, TaskStatus status, Pageable pageable);

}
