package com.klef.fsd.sdp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.klef.fsd.sdp.model.Admin;
import com.klef.fsd.sdp.model.Manager;
import com.klef.fsd.sdp.model.User;
import com.klef.fsd.sdp.repository.AdminRepository;
import com.klef.fsd.sdp.repository.ManagerRepository;
import com.klef.fsd.sdp.repository.UserRepository;


@Service
public class AdminServiceImpl implements AdminService{
	@Autowired
    private AdminRepository adminRepository;
	
	@Autowired
    private ManagerRepository managerRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public Admin checkadminlogin(String username, String password) {
		return adminRepository.findByUsernameAndPassword(username, password);
	}

	@Override
	public String addmanager(Manager manager) {
		managerRepository.save(manager);
		return "Manager Added Successfully";
	}

	@Override
	public List<Manager> displaymanagers() {
		return managerRepository.findAll();
	}

	@Override
	public List<User> displayusers() {
		return userRepository.findAll();
	}

	@Override
	public String deleteuser(int uid) {
	    Optional<User> user = userRepository.findById(uid);
	    
	    if (user.isPresent()) {	
	        userRepository.deleteById(uid);
	        return "User Deleted Successfully";
	    } else {
	        return "User ID Not Found";
	    }
	}
	
	@Override
	public String deletemanager(int nid) {
	    managerRepository.deleteById(nid);
	    return "Manager Deleted Successfully";
	}
	
}


