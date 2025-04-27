package com.klef.fsd.sdp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="user_table")
public class User 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private int id;
    
    @Column(name="user_name", length = 50, nullable = false)
    private String name;
    
    @Column(name="user_gender", length = 10, nullable = false)
    private String gender;
    
    @Column(name="user_dob", length = 20, nullable = false)
    private String dob;
    
    @Column(name="user_email", length = 50, nullable = false, unique = true)
    private String email;
    
    @Column(name="user_username", length = 50, nullable = false, unique = true)
    private String username;
    
    @Column(name="user_password", length = 50, nullable = false)
    private String password;
    
    @Column(name="user_mobileno", length = 20, nullable = false, unique = true)
    private String mobileno;
    
    @Column(name="user_location", length = 50, nullable = false)
    private String location;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Optional: toString() method
    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", gender=" + gender + ", dob=" + dob + ", email=" + email
                + ", username=" + username + ", password=" + password + ", mobileno=" + mobileno + ", location="
                + location + "]";
    }
}