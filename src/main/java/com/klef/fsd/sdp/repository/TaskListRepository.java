package com.klef.fsd.sdp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.klef.fsd.sdp.model.Manager;
import com.klef.fsd.sdp.model.TaskList;
import com.klef.fsd.sdp.model.User;

@Repository
public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    List<TaskList> findByUser(User user);
    List<TaskList> findByManager(Manager manager);
}