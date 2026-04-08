//package com.ds.app.repository;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import com.ds.app.entity.AppUser;
//import com.ds.app.entity.UserRole;
//
//@Repository
//public interface iAppUserRepository extends JpaRepository<AppUser,Integer>{
//
//	public Optional<AppUser> findUsername(String username);
//
//	public Optional<AppUser> findByUsername(String username);
//	public List<AppUser> findByRole(UserRole role);
//}



package com.ds.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.AppUser;
import com.ds.app.entity.UserRole;

@Repository
public interface iAppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);
    public List<AppUser> findByRole(UserRole role);
}
