package com.klef.fsd.sdp.controller;

import java.util.List;
import java.util.Map;

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
import com.klef.fsd.sdp.model.Task;
import com.klef.fsd.sdp.model.TaskList;
import com.klef.fsd.sdp.repository.TaskListRepository;
import com.klef.fsd.sdp.repository.TaskRepository;
import com.klef.fsd.sdp.service.ManagerService;
import com.klef.fsd.sdp.service.TaskService;

@RestController
@RequestMapping("/manager/tasks")
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
public class ManagerTaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ManagerService managerService;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskListRepository taskListRepository;

    @PostMapping("/lists")
    public ResponseEntity<TaskList> createList(@RequestParam String name, @RequestParam String username) {
        Manager manager = managerService.getManagerByUsername(username);
        TaskList list = taskService.createListForManager(name, manager);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/lists")
    public ResponseEntity<List<TaskList>> getManagerLists(@RequestParam String username) {
        Manager manager = managerService.getManagerByUsername(username);
        List<TaskList> lists = taskService.getManagerLists(manager);
        return ResponseEntity.ok(lists);
    }

    @PostMapping("")
    public ResponseEntity<?> createTask(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam Long listId,
            @RequestParam String username) {
        
        try {
            Manager manager = managerService.getManagerByUsername(username);
            if (manager == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Manager not found");
            }
            
            Task task = taskService.createTaskForManager(title, description, listId, manager);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating task: " + e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<List<Task>> getTasksByList(
            @RequestParam Long listId,
            @RequestParam String username) {

        Manager manager = managerService.getManagerByUsername(username);
        List<Task> tasks = taskService.getTasksByListForManager(listId, manager);
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            @RequestParam String username) {

        Manager manager = managerService.getManagerByUsername(username);
        taskService.deleteTaskForManager(taskId, manager);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lists/{listId}")
    public ResponseEntity<?> deleteList(
            @PathVariable Long listId,
            @RequestParam String username) {

        try {
            Manager manager = managerService.getManagerByUsername(username);
            if (manager == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Manager not found");
            }

            TaskList list = taskListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

            if (list.getManager() == null || list.getManager().getId() != manager.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You don't have permission to delete this list");
            }

            taskService.deleteListForManager(listId, manager);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to delete list: " + e.getMessage());
        }
    }


    @PutMapping("/lists/{listId}")
    public ResponseEntity<TaskList> updateList(
            @PathVariable Long listId,
            @RequestBody TaskList updatedList,
            @RequestParam String username) {

        Manager manager = managerService.getManagerByUsername(username);
        TaskList list = taskService.updateListForManager(listId, updatedList, manager);
        return ResponseEntity.ok(list);
    }
    
    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long taskId,
            @RequestBody Task updatedTask,
            @RequestParam String username) {

        try {
            Manager manager = managerService.getManagerByUsername(username);
            Task task = taskService.updateTaskForManager(taskId, updatedTask, manager);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{taskId}/metadata")
    public ResponseEntity<Task> updateTaskMetadata(
            @PathVariable Long taskId,
            @RequestBody Task updatedTask,
            @RequestParam String username) {

        try {
            Manager manager = managerService.getManagerByUsername(username);
            Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

            if (existingTask.getList().getManager() == null || 
                existingTask.getList().getManager().getId() != manager.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Only update specific fields
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{taskId}/description")
    public ResponseEntity<?> updateTaskDescription(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> requestBody,
            @RequestParam String username) {

        try {
            String description = requestBody.get("description");
            if (description == null) {
                return ResponseEntity.badRequest().body("Description is required");
            }

            Manager manager = managerService.getManagerByUsername(username);
            Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

            if (existingTask.getList().getManager() == null || 
                existingTask.getList().getManager().getId() != manager.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            existingTask.setDescription(description);
            Task savedTask = taskRepository.save(existingTask);
            return ResponseEntity.ok(savedTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Task>> getAllManagerTasks(@RequestParam String username) {
        Manager manager = managerService.getManagerByUsername(username);
        List<Task> tasks = taskService.getAllTasksForManager(manager);
        return ResponseEntity.ok(tasks);
    }
}
