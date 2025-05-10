package com.klef.fsd.sdp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klef.fsd.sdp.model.Manager;
import com.klef.fsd.sdp.model.TaskAssignment;
import com.klef.fsd.sdp.model.TaskAssignmentDTO;
import com.klef.fsd.sdp.model.User;
import com.klef.fsd.sdp.repository.TaskAssignmentRepository;
import com.klef.fsd.sdp.service.ManagerService;
import com.klef.fsd.sdp.service.UserService;

@RestController
@RequestMapping("/assignments")
@CrossOrigin(origins = "http://localhost:2000", allowCredentials = "true")
public class TaskAssignmentController {
    
    @Autowired
    private TaskAssignmentRepository assignmentRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ManagerService managerService;

    @PostMapping
    public ResponseEntity<?> assignTask(
            @RequestBody TaskAssignmentDTO assignmentDTO,
            @RequestParam String managerUsername) {
        
        try {
            Manager manager = managerService.getManagerByUsername(managerUsername);
            if (manager == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Manager not found");
            }

            User user = userService.getUserById(assignmentDTO.getUserId());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            
            TaskAssignment assignment = new TaskAssignment();
            assignment.setTitle(assignmentDTO.getTitle());
            assignment.setDescription(assignmentDTO.getDescription());
            assignment.setPriority(assignmentDTO.getPriority());
            assignment.setDeadline(assignmentDTO.getDeadline());
            assignment.setAssignedBy(manager);
            assignment.setAssignedTo(user);
            assignment.setStatus("Pending");
            
            TaskAssignment saved = assignmentRepository.save(assignment);
            return ResponseEntity.ok(saved);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error assigning task: " + e.getMessage());
        }
    }

    @GetMapping("/manager")
    public ResponseEntity<?> getManagerAssignments(@RequestParam String username) {
        try {
            Manager manager = managerService.getManagerByUsername(username);
            if (manager == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Manager not found");
            }
            List<TaskAssignment> tasks = assignmentRepository.findByAssignedBy(manager);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserTasks(@RequestParam String username) {
        try {
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            List<TaskAssignment> tasks = assignmentRepository.findByAssignedTo(user);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String response,
            @RequestParam String username) {
        
        try {
            User user = userService.getUserByUsername(username);
            TaskAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
            
            if (assignment.getAssignedTo().getId()!=user.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            assignment.setStatus(status);
            if (response != null) {
                assignment.setUserResponse(response);
            }
            
            TaskAssignment updated = assignmentRepository.save(assignment);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(
            @PathVariable Long id,
            @RequestBody TaskAssignmentDTO updatedTask,
            @RequestParam String username) {
        
        try {
            TaskAssignment existingTask = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
            
            // Verify if the user is either the manager who assigned or the user who was assigned
            Manager manager = managerService.getManagerByUsername(username);
            User user = userService.getUserByUsername(username);
            
            if (manager == null && user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            
            if (manager != null && !existingTask.getAssignedBy().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the assigning manager can update this task");
            }
            
            // Update fields
            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setPriority(updatedTask.getPriority());
            existingTask.setDeadline(updatedTask.getDeadline());
            existingTask.setStatus(updatedTask.getStatus());
            
            if (updatedTask.getUserResponse() != null) {
                existingTask.setUserResponse(updatedTask.getUserResponse());
            }
            
            TaskAssignment updated = assignmentRepository.save(existingTask);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating task: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(
            @PathVariable Long id,
            @RequestParam String username) {
        
        try {
            TaskAssignment task = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
            
            Manager manager = managerService.getManagerByUsername(username);
            if (manager == null || !task.getAssignedBy().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the assigning manager can delete this task");
            }
            
            assignmentRepository.delete(task);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting task: " + e.getMessage());
        }
    }
}