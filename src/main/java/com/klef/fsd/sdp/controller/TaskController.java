package com.klef.fsd.sdp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klef.fsd.sdp.model.Task;
import com.klef.fsd.sdp.model.TaskList;
import com.klef.fsd.sdp.model.User;
import com.klef.fsd.sdp.service.TaskService;
import com.klef.fsd.sdp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:2000", allowCredentials = "true")
public class TaskController {
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private UserService userService;
    
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
        
        User user = userService.getUserByUsername(username);
        Task task = taskService.createTask(title, description, listId, user);
        return ResponseEntity.ok(task);
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
    public ResponseEntity<Void> deleteList(
            @PathVariable Long listId, 
            @RequestParam String username) {
        
        User user = userService.getUserByUsername(username);
        taskService.deleteList(listId, user);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long taskId,
            @RequestBody Task updatedTask,
            @RequestParam String username) {
        
        User user = userService.getUserByUsername(username);
        Task task = taskService.updateTask(taskId, updatedTask, user);
        return ResponseEntity.ok(task);
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
}