package com.authentication.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authentication.entity.User;
import com.authentication.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/user")
public class UserController {
	@Autowired
	private final UserRepository userRepository;
	@Autowired
	private final BCryptPasswordEncoder passwordEncoder;

	public UserController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		return ResponseEntity.ok("User registered successfully.");
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody User user) {
	    User storedUser = userRepository.findByEmailId(user.getEmailId());
	    if (storedUser != null && passwordEncoder.matches(user.getPassword(), storedUser.getPassword())) {
	        return generateLoginResponse(storedUser, user.getEmailId(), storedUser.getRole());
	    } else {
	        return ResponseEntity.status(401).body("Invalid email or password.");
	    }
	}

	@GetMapping("/getAllUsers")
	public ResponseEntity<List<User>> getAllUsers() {
	    return ResponseEntity.ok(userRepository.findAll());
	}

	private ResponseEntity<Map<String, Object>> generateLoginResponse(User storedUser, String email, String role) {
	    String token = generateToken(storedUser);
	    Map<String, Object> response = new HashMap<>();
	    response.put("token", token);
	    response.put("userName", storedUser.getUsername());
	    response.put("role", role);
	    response.put("email", email);
	    return ResponseEntity.ok(response);
	}

	private String generateToken(User user) {
		return Jwts.builder()
				.setSubject(user.getEmailId())
				.claim("userName", user.getUsername())
				.claim("role", user.getRole())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Token valid for 1 day
				.signWith(SignatureAlgorithm.HS512, "cd8b9a95db1f6e390ff0c31a95bc6ebf5274fc21c5e8acce403474a5f23b9510e2f4ad763cf9d49481e3f514c296d35a6b503bd6e6ffb3242b64ad9a5c6c9a25") // Replace with your secret key
				.compact();
	}

	public boolean isUserAuthenticated(String token) {
		try {
			Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey("cd8b9a95db1f6e390ff0c31a95bc6ebf5274fc21c5e8acce403474a5f23b9510e2f4ad763cf9d49481e3f514c296d35a6b503bd6e6ffb3242b64ad9a5c6c9a25").build().parseClaimsJws(token);
			String role = (String) claimsJws.getBody().get("role");
			return "USER".equals(role);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
