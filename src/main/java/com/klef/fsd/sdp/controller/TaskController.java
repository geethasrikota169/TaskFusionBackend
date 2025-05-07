package com.klef.fsd.sdp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klef.fsd.sdp.model.Task;
import com.klef.fsd.sdp.model.TaskList;
import com.klef.fsd.sdp.model.User;
import com.klef.fsd.sdp.repository.TaskRepository;
import com.klef.fsd.sdp.service.TaskService;
import com.klef.fsd.sdp.service.UserService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:2000", allowCredentials = "true")
public class TaskController {
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TaskRepository taskRepository;
    
    
    @PostMapping("/lists")
    public ResponseEntity<TaskList> createList(@RequestParam String name, @RequestParam String username) {
        User user = userService.getUserByUsername(username);
        TaskList list = taskService.createList(name, user);
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/lists")
    public ResponseEntity<List<TaskList>> getUserLists(@RequestParam String username) {
        System.out.println("Fetching lists for user: " + username);
        User user = userService.getUserByUsername(username);
        if (user == null) {
            System.out.println("User not found");
            return ResponseEntity.notFound().build();
        }
        
        List<TaskList> lists = taskService.getUserLists(user);
        System.out.println("Found lists: " + lists.size());
        
        // Add this to see the JSON output
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println("Lists JSON: " + mapper.writeValueAsString(lists));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(lists);
    }
    
    @PostMapping("")
    public ResponseEntity<Task> createTask(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam Long listId,
            @RequestParam String username) {
        
        try {
            User user = userService.getUserByUsername(username);
            Task task = taskService.createTask(title, description, listId, user);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            // Add proper error logging
            System.err.println("Error creating task: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("")
    public ResponseEntity<List<Task>> getTasksByList(
            @RequestParam Long listId,
            @RequestParam String username) {
        
        User user = userService.getUserByUsername(username);
        List<Task> tasks = taskService.getTasksByList(listId, user);
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId, 
            @RequestParam String username) {
        
        User user = userService.getUserByUsername(username);
        taskService.deleteTask(taskId, user);
        return ResponseEntity.ok().build();
    }

    
    @DeleteMapping("/lists/{listId}")
    public ResponseEntity<?> deleteList(
            @PathVariable Long listId, 
            @RequestParam String username) {
        
        try {
            User user = userService.getUserByUsername(username);
            
            TaskList list = taskService.getListById(listId);
            if (list == null) {
                return ResponseEntity.notFound().build();
            }
            
            if (list.getUser().getId() != user.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You don't have permission to delete this list");
            }
            List<Task> tasksInList = taskService.getTasksByList(listId, user);
            for (Task task : tasksInList) {
                taskRepository.delete(task);
            }
            
            taskService.deleteList(listId, user);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to delete list: " + e.getMessage());
        }
    }
    
    @PutMapping("/{taskId}")
    @Transactional
    public ResponseEntity<Task> updateTask(
            @PathVariable Long taskId,
            @RequestBody Task updatedTask,
            @RequestParam String username) {
        
        Task existingTask = taskRepository.findById(taskId)
            .orElseThrow(() -> new NoSuchElementException("Task not found"));
        
        User user = userService.getUserByUsername(username);
        if (existingTask.getList().getUser().getId()!=user.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        if (updatedTask.getTitle() != null) {
            existingTask.setTitle(updatedTask.getTitle());
        }
        if (updatedTask.getDescription() != null) {
            existingTask.setDescription(updatedTask.getDescription());
        }
        if (updatedTask.getStatus() != null) {
            existingTask.setStatus(updatedTask.getStatus());
        }
        if (updatedTask.getPriority() != null) {
            existingTask.setPriority(updatedTask.getPriority());
        }
        if (updatedTask.getDeadline() != null) {
            existingTask.setDeadline(updatedTask.getDeadline());
        }
        existingTask.setCompleted(updatedTask.isCompleted());
        
        // 4. Save and return
        Task savedTask = taskRepository.save(existingTask);
        return ResponseEntity.ok(savedTask);
    }

    @PutMapping("/lists/{listId}")
    public ResponseEntity<TaskList> updateList(
            @PathVariable Long listId,
            @RequestBody TaskList updatedList,
            @RequestParam String username) {
        
        User user = userService.getUserByUsername(username);
        TaskList list = taskService.updateList(listId, updatedList, user);
        return ResponseEntity.ok(list);
    }
    
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Task> updateStatus(
        @PathVariable Long taskId, 
        @RequestParam String status,
        @RequestParam String username) {
        
        try {
            // Validate status input
            if (!Arrays.asList("none", "inprogress", "completed").contains(status)) {
                return ResponseEntity.badRequest().build();
            }

            User user = userService.getUserByUsername(username);
            Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));

            // Verify task belongs to user
            if (existingTask.getList().getUser().getId()!= user.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Update status
            existingTask.setStatus(status);
            Task updatedTask = taskRepository.save(existingTask);
            
            return ResponseEntity.ok(updatedTask);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PatchMapping("/{taskId}/deadline")
    public ResponseEntity<?> updateDeadline(
        @PathVariable Long taskId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
        @RequestParam String username) {

        try {
            if (deadline.isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest().body("Deadline cannot be in the past");
            }

            User user = userService.getUserByUsername(username);
            Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));

            if (existingTask.getList().getUser().getId() != user.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            existingTask.setDeadline(deadline);
            Task updatedTask = taskRepository.save(existingTask);

            return ResponseEntity.ok(updatedTask);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PatchMapping("/{taskId}/priority")
    public ResponseEntity<Task> updatePriority(
        @PathVariable Long taskId,
        @RequestParam int priority,
        @RequestParam String username) {
        
        try {
            // Validate priority range (0-3)
            if (priority < 0 || priority > 3) {
                return ResponseEntity.badRequest().build();
            }

            User user = userService.getUserByUsername(username);
            Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));

            // Verify task belongs to user
            if (existingTask.getList().getUser().getId()!=user.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Update priority
            existingTask.setPriority(priority);
            Task updatedTask = taskRepository.save(existingTask);
            
            return ResponseEntity.ok(updatedTask);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
 // For updating task metadata (title, status, priority, deadline)
    @PutMapping("/{taskId}/metadata")
    public ResponseEntity<Task> updateTaskMetadata(
            @PathVariable Long taskId,
            @RequestBody Task updatedTask,
            @RequestHeader("username") String username) {
        
        Task existingTask = taskRepository.findById(taskId)
            .orElseThrow(() -> new NoSuchElementException("Task not found"));
        
        User user = userService.getUserByUsername(username);
        if (existingTask.getList().getUser().getId() != user.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Only update these specific fields
        if (updatedTask.getTitle() != null) {
            existingTask.setTitle(updatedTask.getTitle());
        }
        if (updatedTask.getStatus() != null) {
            existingTask.setStatus(updatedTask.getStatus());
        }
        if (updatedTask.getPriority() != null) {
            existingTask.setPriority(updatedTask.getPriority());
        }
        if (updatedTask.getDeadline() != null) {
            existingTask.setDeadline(updatedTask.getDeadline());
        }
        
        Task savedTask = taskRepository.save(existingTask);
        return ResponseEntity.ok(savedTask);
    }

    // For updating task description only
    @PutMapping("/{taskId}/description")
    public ResponseEntity<?> updateTaskDescription(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> requestBody,
            @RequestHeader("username") String username) {
        
        try {
            String description = requestBody.get("description");
            if (description == null) {
                return ResponseEntity.badRequest().body("Description is required");
            }

            Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));

            User user = userService.getUserByUsername(username);
            if (existingTask.getList().getUser().getId() != user.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            existingTask.setDescription(description);
            Task savedTask = taskRepository.save(existingTask);
            
            return ResponseEntity.ok(savedTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}