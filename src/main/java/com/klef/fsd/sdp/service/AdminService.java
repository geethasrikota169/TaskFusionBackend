package com.klef.fsd.sdp.service;

import java.util.List;

import com.klef.fsd.sdp.model.Admin;
import com.klef.fsd.sdp.model.User;

public interface AdminService 
{
    Admin checkadminlogin(String username, String password);
    
//    String addeventmanager(Manager manager);
//    List<Manager> displayeventmanagers();
    
    List<User> displayusers();
    String deleteuser(int cid);
    
//    String deletemanager(int nid); 
}
