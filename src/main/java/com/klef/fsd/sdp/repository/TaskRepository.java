package com.klef.fsd.sdp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.klef.fsd.sdp.model.Task;
import com.klef.fsd.sdp.model.TaskList;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
	 List<Task> findByList(TaskList list);
	 void deleteAllByList(TaskList list); 
}