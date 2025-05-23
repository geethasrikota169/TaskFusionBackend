package com.klef.fsd.sdp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klef.fsd.sdp.model.Admin;
import com.klef.fsd.sdp.model.Manager;
import com.klef.fsd.sdp.model.User;
import com.klef.fsd.sdp.service.AdminService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
public class AdminController 
{
  @Autowired
  private AdminService adminService;
  
  
  @PostMapping("/checkadminlogin")
  public ResponseEntity<?> checkadminlogin(@RequestBody Admin admin)
  {
	  try 
      {
          Admin a = adminService.checkadminlogin(admin.getUsername(), admin.getPassword());

          if (a!=null) 
          {
              return ResponseEntity.ok(a); // if login is successful
          } 
          else 
          {
              return ResponseEntity.status(401).body("Invalid Username or Password"); // if login is fail
          }
      } 
      catch (Exception e) 
      {
          return ResponseEntity.status(500).body("Login failed: " + e.getMessage());
      }
  }
  
  @GetMapping("/viewallusers")
  public ResponseEntity<List<User>> viewallusers()
  {
	 List<User> users =  adminService.displayusers();
	 
	 return ResponseEntity.ok(users); // 200 - success
  }
  
  @PostMapping("/addmanager")
  public ResponseEntity<String> addmanager(@RequestBody Manager manager)
  {
	   try
	   {
		  String output = adminService.addmanager(manager);
		  return ResponseEntity.ok(output); // 200 - success
	   }
	   catch(Exception e)
	   {
		   
		   return ResponseEntity.status(500).body("Failed to Add Manager ... !!"); 
	   }
  }
  
  @GetMapping("/viewallmanagers")
  public ResponseEntity<List<Manager>> viewallmanagers()
  {
	 List<Manager> managers =  adminService.displaymanagers();
	 
	 return ResponseEntity.ok(managers); // 200 - success
  }
  
  @DeleteMapping("/deleteuser")
  public ResponseEntity<String> deleteuser(@RequestParam int uid)
  {
      try
      {
          String output = adminService.deleteuser(uid);
          return ResponseEntity.ok(output);
      }
      catch(Exception e)
      {
          return ResponseEntity.status(500).body("Failed to Delete User ... !!"); 
      }
  }
  
  @DeleteMapping("/deletemanager/{nid}")
  public ResponseEntity<String> deletemanager(@PathVariable int nid)
  {
      try
      {
          String output = adminService.deletemanager(nid);
          return ResponseEntity.ok(output);
      }
      catch(Exception e)
      {
          return ResponseEntity.status(500).body("Failed to Delete Manager ... !!");
      }
  }


  
  
  
}
