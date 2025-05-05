package com.klef.fsd.sdp.service;

import com.klef.fsd.sdp.model.Manager;
import com.klef.fsd.sdp.model.Task;
import com.klef.fsd.sdp.model.TaskList;
import com.klef.fsd.sdp.model.User;
import com.klef.fsd.sdp.repository.TaskListRepository;
import com.klef.fsd.sdp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskListRepository taskListRepository;

    public TaskList createList(String name, User user) {
        TaskList list = new TaskList();
        list.setName(name);
        list.setUser(user);
        return taskListRepository.save(list);
    }

    public List<TaskList> getUserLists(User user) {
        return taskListRepository.findByUser(user);
    }

    public Task createTask(String title, String description, Long listId, User user) {
        TaskList list = taskListRepository.findById(listId)
            .orElseThrow(() -> new RuntimeException("List not found"));

        if (list.getUser().getId() != user.getId()) {
            throw new RuntimeException("Unauthorized access to list");
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setCompleted(false);
        task.setList(list);

        Task savedTask = taskRepository.save(task);
        list.getTasks().add(savedTask);
        return savedTask;
    }

    public List<Task> getTasksByList(Long listId, User user) {
        TaskList list = taskListRepository.findById(listId)
            .orElseThrow(() -> new RuntimeException("List not found"));

        if (list.getUser().getId() != user.getId()) {
            throw new RuntimeException("Unauthorized access to list");
        }

        return taskRepository.findByList(list);
    }

    public void deleteTask(Long taskId, User user) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getList().getUser().getId() != user.getId()) {
            throw new RuntimeException("Unauthorized access to task");
        }

        taskRepository.delete(task);
    }

    public Task updateTask(Long taskId, Task updatedTask, User user) {
        Task existingTask = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

        if (existingTask.getList().getUser().getId() != user.getId()) {
            throw new RuntimeException("Unauthorized access to task");
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setCompleted(updatedTask.isCompleted());

        return taskRepository.save(existingTask);
    }

    public void deleteList(Long listId, User user) {
        TaskList list = taskListRepository.findById(listId)
            .orElseThrow(() -> new RuntimeException("List not found"));

        if (list.getUser().getId() != user.getId()) {
            throw new RuntimeException("Unauthorized access to list");
        }

        taskRepository.deleteAllByList(list);
        taskListRepository.delete(list);
    }

    public TaskList updateList(Long listId, TaskList updatedList, User user) {
        TaskList existingList = taskListRepository.findById(listId)
            .orElseThrow(() -> new RuntimeException("List not found"));

        if (existingList.getUser().getId() != user.getId()) {
            throw new RuntimeException("Unauthorized access to list");
        }

        existingList.setName(updatedList.getName());
        return taskListRepository.save(existingList);
    }
    
    public TaskList createListForManager(String name, Manager manager) {
        TaskList list = new TaskList();
        list.setName(name);
        list.setManager(manager);
        return taskListRepository.save(list);
    }

    public List<TaskList> getManagerLists(Manager manager) {
        return taskListRepository.findByManager(manager);
    }
    
    public Task createTaskForManager(String title, String description, Long listId, Manager manager) {
        TaskList list = taskListRepository.findById(listId)
            .orElseThrow(() -> new RuntimeException("List not found"));

        if (list.getManager() == null || list.getManager().getId()!=manager.getId()) {
            throw new RuntimeException("Unauthorized access to list");
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setCompleted(false);
        task.setList(list);

        Task savedTask = taskRepository.save(task);
        list.getTasks().add(savedTask);
        return savedTask;
    }

    public List<Task> getTasksByListForManager(Long listId, Manager manager) {
        TaskList list = taskListRepository.findById(listId)
            .orElseThrow(() -> new RuntimeException("List not found"));

        if (list.getManager() == null || list.getManager().getId()!= manager.getId()) {
            throw new RuntimeException("Unauthorized access to list");
        }

        return taskRepository.findByList(list);
    }

    public void deleteTaskForManager(Long taskId, Manager manager) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getList().getManager() == null || task.getList().getManager().getId() != manager.getId()) {
            throw new RuntimeException("Unauthorized access to task");
        }

        taskRepository.delete(task);
    }

    public Task updateTaskForManager(Long taskId, Task updatedTask, Manager manager) {
        Task existingTask = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

        if (existingTask.getList().getManager() == null || existingTask.getList().getManager().getId() != manager.getId()) {
            throw new RuntimeException("Unauthorized access to task");
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setCompleted(updatedTask.isCompleted());

        return taskRepository.save(existingTask);
    }

    public void deleteListForManager(Long listId, Manager manager) {
        TaskList list = taskListRepository.findById(listId)
            .orElseThrow(() -> new RuntimeException("List not found"));

        if (list.getManager() == null || list.getManager().getId() != manager.getId()) {
            throw new RuntimeException("Unauthorized access to list");
        }

        taskRepository.deleteAllByList(list);
        taskListRepository.delete(list);
    }

    public TaskList updateListForManager(Long listId, TaskList updatedList, Manager manager) {
        TaskList existingList = taskListRepository.findById(listId)
            .orElseThrow(() -> new RuntimeException("List not found"));

        if (existingList.getManager() == null || existingList.getManager().getId()!=manager.getId()) {
            throw new RuntimeException("Unauthorized access to list");
        }

        existingList.setName(updatedList.getName());
        return taskListRepository.save(existingList);
    }

}
