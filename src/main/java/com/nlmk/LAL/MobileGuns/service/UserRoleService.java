package com.nlmk.LAL.MobileGuns.service;

import java.util.List;

import com.nlmk.LAL.MobileGuns.entity.Role;

public interface UserRoleService {

	public List<Role> getUserRoles(String userId) throws Exception;

}

