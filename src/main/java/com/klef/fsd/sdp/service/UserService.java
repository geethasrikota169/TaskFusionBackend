package com.klef.fsd.sdp.service;

import com.klef.fsd.sdp.model.User;

public interface UserService 
{
  public String userRegistration(User user);
  public User checkUserLogin(String username, String password);
  public User getUserByUsername(String username);
}
