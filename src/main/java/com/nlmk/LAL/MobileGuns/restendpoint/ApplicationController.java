package com.nlmk.LAL.MobileGuns.restendpoint;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nlmk.LAL.MobileGuns.entity.Role;
import com.nlmk.LAL.MobileGuns.entity.User;
import com.nlmk.LAL.MobileGuns.repository.UserRepository;
import com.nlmk.LAL.MobileGuns.security.JwtUtils;
import com.nlmk.LAL.MobileGuns.service.UserRoleService;
import com.nlmk.LAL.MobileGuns.tools.MyTools;

@Controller
@RequestMapping("/api/application")
@CrossOrigin
public class ApplicationController {

	@Value("${user_guide_complete_path}")
	private String userGuideCompletePath;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private AuthenticationManager authenticationManager;



	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User user) {

		final String INVALID_USER_OR_PASSWORD = "Invalid user or password";
		final String USER_NOT_ALLOWED = "User not allowed to use GESFAB Web";

		MyTools.logInfo("User : " + user.getUserId());

		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getUserId(), user.getPassword()));

			if (authentication.isAuthenticated()) {

				LdapUserDetailsImpl springUser = (LdapUserDetailsImpl) authentication.getPrincipal();

				// Check if the user (in lowercase) has access to GESFAB Web :
				//
				// - Check if the user exists in the table UTILITIES.TUT005_USR. If not found or
				// not active, throw an exception
				//
				// - If found and active, retrieve its roles. If no roles found, throw an
				// exception

				String userInLowerCase = springUser.getUsername().toLowerCase();

				User userToFind = userRepository.findByUserId(userInLowerCase);

				MyTools.logInfo("userToFind : " + userToFind);

				if (userToFind == null || !userToFind.getIsActive().equals("Y"))
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(USER_NOT_ALLOWED);

				List<String> roles = new ArrayList<>();

				try {
					for (Role role : userRoleService.getUserRoles(userInLowerCase)) {
						roles.add(role.getRole());
					}
				} catch (Exception e) {
					MyTools.logError("ApplicationController - login exception: " + e.getMessage(), e);

					throw new UsernameNotFoundException("Role not found for user id " + userInLowerCase);
				}

				MyTools.logInfo("roles.size(): " + roles.size());

				if (roles.size() == 0)
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(USER_NOT_ALLOWED);

				Map<String, Object> authData = new HashMap<>();

				authData.put("token", jwtUtils.generateToken(user.getUserId(), userToFind.getUserName(), roles));
				authData.put("type", "Bearer");

				return ResponseEntity.ok(authData);
			}

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_USER_OR_PASSWORD);

		} catch (BadCredentialsException e) {
			MyTools.logError("BadCredentialsException - Error when login - Cause : " + e.getCause());
			MyTools.logError("BadCredentialsException - Error when login - Message : " + e.getMessage());

			e.printStackTrace();

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_USER_OR_PASSWORD);
		} catch (Exception e) {
			MyTools.logError("Error when login - Cause : " + e.getCause());
			MyTools.logError("Error when login - Message : " + e.getMessage());

			e.printStackTrace();

			if (e instanceof LockedException)
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}

	@GetMapping("/get-user-guide")
	public ResponseEntity<Resource> getUserGuide() throws Exception {

		MyTools.logInfo("userGuideCompletePath : " + userGuideCompletePath);

		if (userGuideCompletePath == null)
			throw new NullPointerException("User guide file missing");

		File file = new File(userGuideCompletePath);

		Path path = Paths.get(file.getAbsolutePath());

		ByteArrayResource resource = null;

		resource = new ByteArrayResource(Files.readAllBytes(path));

		HttpHeaders headers = new HttpHeaders();

		headers.add("Content-Disposition", "attachment; filename=\"GESFAB-WEB-User-Guide.pdf\"");

		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.parseMediaType("application/pdf")).body(resource);
	}
	
	@GetMapping("/get-db-environment")
	public ResponseEntity<Map<String, String>> getDbEnvironment() {
	    Map<String, String> response = new HashMap<>();
	    response.put("environment", "DEV");
	    return ResponseEntity.ok(response);
	}

}
