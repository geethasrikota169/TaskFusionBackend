package com.klef.fsd.sdp.service;

import java.util.List;

import com.klef.fsd.sdp.model.User;

public interface UserService 
{
  public String userRegistration(User user);
  public User checkUserLogin(String username, String password);
  public User getUserByUsername(String username);
  public User getUserById(Long id);
  List<User> getAllUsers();
}
