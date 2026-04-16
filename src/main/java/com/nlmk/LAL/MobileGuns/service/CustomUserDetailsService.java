 package com.nlmk.LAL.MobileGuns.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nlmk.LAL.MobileGuns.entity.Role;
import com.nlmk.LAL.MobileGuns.entity.User;
import com.nlmk.LAL.MobileGuns.repository.UserRepository;
import com.nlmk.LAL.MobileGuns.tools.MyTools;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserRoleService userRoleService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		MyTools.logInfo("username : " + username);

		User user = userRepository.findByUserId(username);

		if (user == null)
			throw new UsernameNotFoundException("User not found for user id " + username);

		List<String> roles = new ArrayList<>();

		try {
			for (Role role : userRoleService.getUserRoles(username)) {
				roles.add(role.getRole());
			}
		} catch (Exception e) {
			MyTools.logError("CustomUserDetailsService - loadUserByUsername exception: " + e.getMessage(), e);

			throw new UsernameNotFoundException("Role not found for user id " + username);
		}

		user.setRoles(roles);

		List<GrantedAuthority> roleList = new ArrayList<>();

		user.getRoles().forEach(role -> {
			GrantedAuthority authority = new SimpleGrantedAuthority(role);

			roleList.add(authority);
		});

		return new org.springframework.security.core.userdetails.User(user.getUserId(), "Hidden", roleList);
	}

}

