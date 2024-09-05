package com.authentication.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authentication.entity.Admin;
import com.authentication.entity.User;
import com.authentication.repository.AdminRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
	@Autowired
	private final AdminRepository adminRepository;
	@Autowired
	private final BCryptPasswordEncoder passwordEncoder;

	public AdminController(AdminRepository adminRepository, BCryptPasswordEncoder passwordEncoder) {
		this.adminRepository = adminRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/login")
	public ResponseEntity<?> adminLogin(@RequestBody Admin admin) {
		Admin storedAdmin = adminRepository.findByUsername(admin.getUsername());
		if (storedAdmin != null && passwordEncoder.matches(admin.getPassword(), storedAdmin.getPassword())) {
			return generateLoginResponse(storedAdmin, admin.getUsername(), storedAdmin.getRole());
		} else {
			return ResponseEntity.status(401).body("Invalid admin credentials.");
		}
	}
	
	private ResponseEntity<Map<String, Object>> generateLoginResponse(Admin storedUser, String username, String role) {
	    String token = generateToken(storedUser);
	    Map<String, Object> response = new HashMap<>();
	    response.put("token", token);
	    response.put("userName", storedUser.getUsername());
	    response.put("role", role);
	    return ResponseEntity.ok(response);
	}

	private String generateToken(Admin admin) {
		// Build JWT token
		@SuppressWarnings("deprecation")
		String token = Jwts.builder().setSubject(admin.getUsername()).claim("AdminName", admin.getUsername())

				.claim("userRole", admin.getRole()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Token valid for 1 day
				.signWith(SignatureAlgorithm.HS512,
						"cd8b9a95db1f6e390ff0c31a95bc6ebf5274fc21c5e8acce403474a5f23b9510e2f4ad763cf9d49481e3f514c296d35a6b503bd6e6ffb3242b64ad9a5c6c9a25")
				.compact();
		return token;
	}

}
