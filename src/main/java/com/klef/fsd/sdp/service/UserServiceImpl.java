package com.klef.fsd.sdp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.klef.fsd.sdp.model.User;
import com.klef.fsd.sdp.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService
{
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public String userRegistration(User user) 
    {
        userRepository.save(user);
        return "User Registered Successfully";
    }

    @Override
    public User checkUserLogin(String username, String password) 
    {
        return userRepository.findByUsernameAndPassword(username, password);
    }
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
