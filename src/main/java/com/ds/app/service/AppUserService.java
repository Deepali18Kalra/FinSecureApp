package com.ds.app.service;

import com.ds.app.entity.AppUser;

import java.util.Optional;

public interface AppUserService {

	public AppUser registerAppUser(AppUser user);

    Optional<AppUser> findByUsername(String username);

}
