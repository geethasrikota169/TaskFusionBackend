package com.klef.fsd.sdp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.klef.fsd.sdp.service.ManagerService;
import com.klef.fsd.sdp.service.TaskService;

@RestController
@RequestMapping("/manager/tasks")
@CrossOrigin(origins = "http://localhost:2000", allowCredentials = "true")
public class ManagerTaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ManagerService managerService;

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
    public ResponseEntity<Task> createTask(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam Long listId,
            @RequestParam String username) {

        Manager manager = managerService.getManagerByUsername(username);
        Task task = taskService.createTaskForManager(title, description, listId, manager);
        return ResponseEntity.ok(task);
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
    public ResponseEntity<Void> deleteList(
            @PathVariable Long listId,
            @RequestParam String username) {

        Manager manager = managerService.getManagerByUsername(username);
        taskService.deleteListForManager(listId, manager);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long taskId,
            @RequestBody Task updatedTask,
            @RequestParam String username) {

        Manager manager = managerService.getManagerByUsername(username);
        Task task = taskService.updateTaskForManager(taskId, updatedTask, manager);
        return ResponseEntity.ok(task);
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
}
