package com.authentication.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
@Entity
public class Admin {
	@Id
    private String username;
    private String password;
    private String role = "Admin"; // Default role for all customers
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}

	public Admin(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
    public void setPassword(String password) {
       
        this.password = password;
    }
public Admin() {
	
}
    // Constructors, getters, setters (manually written)
public String getRole() {
	return role;
}
public void setRole(String role) {
	this.role = role;
}
}