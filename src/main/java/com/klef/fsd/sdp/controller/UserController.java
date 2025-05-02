package com.klef.fsd.sdp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klef.fsd.sdp.model.User;
import com.klef.fsd.sdp.repository.UserRepository;
import com.klef.fsd.sdp.service.UserService;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:2000", allowCredentials = "true") 
public class UserController 
{
   @Autowired
   private UserService userService;
   
   @Autowired
   private UserRepository userRepository;
    
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
   
//   @PostMapping("/checkUserLogin")
//   public ResponseEntity<?> checkUserLogin(@RequestBody User user) 
//   {
//       try 
//       {
//           User u = userService.checkUserLogin(user.getUsername(), user.getPassword());
//
//           if (u != null) 
//           {
//               return ResponseEntity.ok(u); // Login successful
//           } 
//           else 
//           {
//               return ResponseEntity.status(401).body("Invalid Username or Password"); // Login failed
//           }
//       } 
//       catch (Exception e) 
//       {
//           return ResponseEntity.status(500).body("Login failed: " + e.getMessage());
//       }
//   }
   @PostMapping("/checkUserLogin")
   public ResponseEntity<User> checkUserLogin(@RequestBody User user) {
       try {
           User u = userService.checkUserLogin(user.getUsername(), user.getPassword());
           if (u != null) {
               // Create a response object with all required fields
               User responseUser = new User();
               responseUser.setId(u.getId());
               responseUser.setUsername(u.getUsername());
               responseUser.setName(u.getName());
               responseUser.setEmail(u.getEmail());
               responseUser.setGender(u.getGender());
               responseUser.setDob(u.getDob());
               responseUser.setMobileno(u.getMobileno());
               responseUser.setLocation(u.getLocation());
               
               return ResponseEntity.ok(responseUser);
           } else {
               return ResponseEntity.status(401).body(null);
           }
       } catch (Exception e) {
           return ResponseEntity.status(500).body(null);
       }
   }
   
   @GetMapping("/profile")
   public ResponseEntity<User> getUserProfile(@RequestParam String username) {
       try {
           User user = userService.getUserByUsername(username);
           if (user != null) {
               return ResponseEntity.ok(user);
           }
           return ResponseEntity.notFound().build();
       } catch (Exception e) {
           return ResponseEntity.status(500).body(null);
       }
   }
   
   @PutMapping("/update")
   public ResponseEntity<User> updateUserProfile(@RequestBody User updatedUser) {
       try {
           User existingUser = userRepository.findByUsername(updatedUser.getUsername());
           if (existingUser == null) {
               return ResponseEntity.notFound().build();
           }

           // Only update allowed fields
           existingUser.setName(updatedUser.getName());
           existingUser.setGender(updatedUser.getGender());
           existingUser.setDob(updatedUser.getDob());
           existingUser.setMobileno(updatedUser.getMobileno());
           existingUser.setLocation(updatedUser.getLocation());

           User savedUser = userRepository.save(existingUser);
           return ResponseEntity.ok(savedUser);
       } catch (Exception e) {
           return ResponseEntity.status(500).build();
       }
   }
}
