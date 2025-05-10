package com.klef.fsd.sdp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.klef.fsd.sdp.model.User;


@Repository
public interface UserRepository extends JpaRepository<User,Integer>
{
  public User findByUsernameAndPassword(String username, String password);
  public User findByUsername(String username);
  Optional<User> findById(Long id);
}
