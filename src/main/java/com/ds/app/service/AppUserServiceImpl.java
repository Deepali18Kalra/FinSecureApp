package com.ds.app.service;

import com.ds.app.entity.AppUser;
import com.ds.app.repository.iAppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }
}
