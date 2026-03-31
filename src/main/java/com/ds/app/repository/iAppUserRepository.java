package com.ds.app.repository;

import com.ds.app.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface iAppUserRepository extends JpaRepository<AppUser,Long>{

	public Optional<AppUser> findByUsername(String username);
}
