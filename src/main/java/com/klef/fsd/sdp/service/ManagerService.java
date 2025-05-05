package com.klef.fsd.sdp.service;

import com.klef.fsd.sdp.model.Manager;

public interface ManagerService {
    Manager checkmanagerlogin(String username, String password);
    Manager getManagerByUsername(String username);
    Manager getManagerProfile(String username);
    Manager updateManagerProfile(Manager manager);
}