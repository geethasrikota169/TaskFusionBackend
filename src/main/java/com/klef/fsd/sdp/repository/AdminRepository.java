package com.klef.fsd.sdp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klef.fsd.sdp.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, String> {


	Admin findByUsernameAndPassword(String username, String password);

}
