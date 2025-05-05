package com.klef.fsd.sdp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.klef.fsd.sdp.model.Manager;


@Repository
public interface ManagerRepository extends JpaRepository<Manager,Integer>
{
  public Manager findByUsernameAndPassword(String username, String password);

  public Manager findByUsername(String username);
}