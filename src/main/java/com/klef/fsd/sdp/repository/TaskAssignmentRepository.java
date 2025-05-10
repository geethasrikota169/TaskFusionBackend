package com.klef.fsd.sdp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klef.fsd.sdp.model.Manager;
import com.klef.fsd.sdp.model.TaskAssignment;
import com.klef.fsd.sdp.model.User;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
    List<TaskAssignment> findByAssignedTo(User user);
    List<TaskAssignment> findByAssignedBy(Manager manager);
}
