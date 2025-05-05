package com.klef.fsd.sdp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.klef.fsd.sdp.model.Manager;
import com.klef.fsd.sdp.repository.ManagerRepository;

@Service
public class ManagerServiceImpl implements ManagerService
{
	@Autowired
    private ManagerRepository managerRepository;
	
	@Override
	public Manager checkmanagerlogin(String username, String password) 
	{
		return managerRepository.findByUsernameAndPassword(username, password);
	}
	
	@Override
	public Manager getManagerByUsername(String username) {
	    return managerRepository.findByUsername(username);
	}

	@Override
	public Manager getManagerProfile(String username) {
	    return managerRepository.findByUsername(username);
	}

	@Override
	public Manager updateManagerProfile(Manager manager) {
	    Manager existingManager = managerRepository.findByUsername(manager.getUsername());
	    if (existingManager != null) {
	        existingManager.setName(manager.getName());
	        existingManager.setGender(manager.getGender());
	        existingManager.setDob(manager.getDob());
	        existingManager.setMobileno(manager.getMobileno());
	        existingManager.setEmail(manager.getEmail());
	        return managerRepository.save(existingManager);
	    }
	    return null;
	}
}
