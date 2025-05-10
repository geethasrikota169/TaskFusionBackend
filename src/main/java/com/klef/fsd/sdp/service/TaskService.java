package com.klef.fsd.sdp.service;

import com.klef.fsd.sdp.model.Manager;

import com.klef.fsd.sdp.model.Task;
import com.klef.fsd.sdp.model.TaskList;
import com.klef.fsd.sdp.model.User;
import com.klef.fsd.sdp.repository.TaskListRepository;
import com.klef.fsd.sdp.repository.TaskRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

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
    
    public TaskList getListById(Long listId) {
        return taskListRepository.findById(listId).orElse(null);
    }

    public List<TaskList> getUserLists(User user) {
        return taskListRepository.findByUser(user);
    }

    @Transactional
    public Task createTask(String title, String description, Long listId, User user) {
        TaskList list = taskListRepository.findById(listId)
            .orElseThrow(() -> new NoSuchElementException("List not found"));

        if (list.getUser().getId()!=(user.getId())) {
            throw new RuntimeException("Unauthorized access to list");
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description != null ? description : "");
        task.setCompleted(false);
        task.setStatus("none");  
        task.setPriority(0);    
        task.setList(list);

        return taskRepository.save(task);
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
            .orElseThrow(() -> new NoSuchElementException("Task not found"));

        if (existingTask.getList().getUser().getId()!=user.getId()) {
            throw new RuntimeException("Unauthorized access to task");
        }

        // Use BeanUtils to copy non-null properties
        BeanUtils.copyProperties(updatedTask, existingTask, 
            getNullPropertyNames(updatedTask));
        
        return taskRepository.save(existingTask);
    }

    
    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
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

        if (list.getManager() == null || list.getManager().getId() != manager.getId()) {
            throw new RuntimeException("Unauthorized access to list");
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description != null ? description : "");
        task.setCompleted(false);
        task.setStatus("none");  
        task.setPriority(0);     
        task.setList(list);

        return taskRepository.save(task);
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

        if (existingTask.getList().getManager() == null || 
            existingTask.getList().getManager().getId() != manager.getId()) {
            throw new RuntimeException("Unauthorized access to task");
        }

        // Copy non-null properties
        BeanUtils.copyProperties(updatedTask, existingTask, getNullPropertyNames(updatedTask));
        return taskRepository.save(existingTask);
    }

    public void deleteListForManager(Long listId, Manager manager) {
        TaskList list = taskListRepository.findById(listId)
            .orElseThrow(() -> new RuntimeException("List not found"));

        if (list.getManager() == null || list.getManager().getId() != manager.getId()) {
            throw new RuntimeException("Unauthorized access to list");
        }

        List<Task> tasksInList = taskRepository.findByList(list);
        taskRepository.deleteAll(tasksInList);
        
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
    
    public List<Task> getAllUserTasks(User user) {
        List<TaskList> userLists = taskListRepository.findByUser(user);
        List<Task> allTasks = new ArrayList<>();
        for (TaskList list : userLists) {
            allTasks.addAll(taskRepository.findByList(list));
        }
        
        return allTasks;
    }
    
    public List<Task> getAllTasksForManager(Manager manager) {
        List<TaskList> managerLists = taskListRepository.findByManager(manager);
        List<Task> allTasks = new ArrayList<>();
        for (TaskList list : managerLists) {
            allTasks.addAll(taskRepository.findByList(list));
        }
        return allTasks;
    }

}
