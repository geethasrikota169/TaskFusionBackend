package com.klef.fsd.sdp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klef.fsd.sdp.model.User;
import com.klef.fsd.sdp.service.UserService;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:2001", allowCredentials = "true") 
public class UserController 
{
   @Autowired
   private UserService userService;
    
   @GetMapping("/")
   public String home()
   {
       return "Welcome to the User Management System";
   }
   
   @PostMapping("/registration")
   public ResponseEntity<String> userRegistration(@RequestBody User user) {
       try {
           String output = userService.userRegistration(user);
           return ResponseEntity.ok(output);
       } catch(DataIntegrityViolationException e) {
           // This will catch database constraint violations
           return ResponseEntity.status(400).body("Registration failed: " + 
               e.getRootCause().getMessage());
       } catch(Exception e) {
           return ResponseEntity.status(500).body("Registration failed: " + 
               e.getMessage());
       }
   }
   
   @PostMapping("/checkUserLogin")
   public ResponseEntity<?> checkUserLogin(@RequestBody User user) 
   {
       try 
       {
           User u = userService.checkUserLogin(user.getUsername(), user.getPassword());

           if (u != null) 
           {
               return ResponseEntity.ok(u); // Login successful
           } 
           else 
           {
               return ResponseEntity.status(401).body("Invalid Username or Password"); // Login failed
           }
       } 
       catch (Exception e) 
       {
           return ResponseEntity.status(500).body("Login failed: " + e.getMessage());
       }
   }
}
