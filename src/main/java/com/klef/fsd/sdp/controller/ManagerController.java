package com.klef.fsd.sdp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klef.fsd.sdp.model.Manager;
import com.klef.fsd.sdp.repository.ManagerRepository;
import com.klef.fsd.sdp.service.ManagerService;

@RestController
@RequestMapping("/manager")
@CrossOrigin(origins = "http://localhost:2000", allowCredentials = "true")
public class ManagerController 
{
   @Autowired
   private ManagerService managerService;
   
   @Autowired 
   private ManagerRepository managerRepository;
	   
   @PostMapping("/checkmanagerlogin")
   public ResponseEntity<Manager> checkmanagerlogin(@RequestBody Manager manager) {
       try {
           Manager m = managerService.checkmanagerlogin(manager.getUsername(), manager.getPassword());

           if (m!=null) {
               // Create a response object with all required fields
               Manager responseManager = new Manager();
               responseManager.setId(m.getId());
               responseManager.setUsername(m.getUsername());
               responseManager.setName(m.getName());
               responseManager.setEmail(m.getEmail());
               responseManager.setGender(m.getGender());
               responseManager.setDob(m.getDob());
               responseManager.setMobileno(m.getMobileno());
               responseManager.setLocation(m.getLocation());
               
               return ResponseEntity.ok(responseManager);
           } 
           else {
               return ResponseEntity.status(401).body(null);
           }
       } 
       catch (Exception e) {
           return ResponseEntity.status(500).body(null);
       }
   }
   
   @GetMapping("/profile")
   public ResponseEntity<Manager> getManagerProfile(@RequestParam String username) {
       try {
           Manager manager = managerService.getManagerByUsername(username);
           if (manager != null) {
               // Create a response object with all required fields
               Manager responseManager = new Manager();
               responseManager.setId(manager.getId());
               responseManager.setUsername(manager.getUsername());
               responseManager.setName(manager.getName());
               responseManager.setEmail(manager.getEmail());
               responseManager.setGender(manager.getGender());
               responseManager.setDob(manager.getDob());
               responseManager.setMobileno(manager.getMobileno());
               responseManager.setLocation(manager.getLocation());
               
               return ResponseEntity.ok(responseManager);
           }
           return ResponseEntity.notFound().build();
       } catch (Exception e) {
           return ResponseEntity.status(500).body(null);
       }
   }

   @PutMapping("/update")
   public ResponseEntity<Manager> updateManagerProfile(@RequestBody Manager updatedManager) {
       try {
           Manager existingManager = managerRepository.findByUsername(updatedManager.getUsername());
           if (existingManager == null) {
               return ResponseEntity.notFound().build();
           }

           // Only update allowed fields
           existingManager.setName(updatedManager.getName());
           existingManager.setGender(updatedManager.getGender());
           existingManager.setDob(updatedManager.getDob());
           existingManager.setMobileno(updatedManager.getMobileno());
           existingManager.setLocation(updatedManager.getLocation());


           Manager savedManager = managerRepository.save(existingManager);
           return ResponseEntity.ok(savedManager);
       } catch (Exception e) {
           return ResponseEntity.status(500).build();
       }
   }

}
