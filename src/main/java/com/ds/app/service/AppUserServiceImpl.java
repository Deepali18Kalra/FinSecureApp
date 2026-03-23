package com.ds.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ds.app.entity.AppUser;
import com.ds.app.repository.iAppUserRepository;

@Service
public class AppUserServiceImpl implements AppUserService{

	@Autowired
	private iAppUserRepository appUserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public AppUser registerAppUser(AppUser user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		AppUser savedUser = appUserRepository.save(user);
		return savedUser;
	}
}
