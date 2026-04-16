package com.nlmk.LAL.MobileGuns.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.web.bind.annotation.CrossOrigin;

import com.nlmk.LAL.MobileGuns.entity.User;


@CrossOrigin // to authorize Angular to use methods
public interface UserRepository extends JpaRepository<User, String> {

	public User findByUserId(String userId);

}

