package com.klef.fsd.sdp.service;

import java.util.List;

import com.klef.fsd.sdp.model.Admin;
import com.klef.fsd.sdp.model.Manager;
import com.klef.fsd.sdp.model.User;

public interface AdminService 
{
    Admin checkadminlogin(String username, String password);
    
    String addmanager(Manager manager);
    List<Manager> displaymanagers();
    
    List<User> displayusers();
    String deleteuser(int uid);
    
    String deletemanager(int nid); 
}
