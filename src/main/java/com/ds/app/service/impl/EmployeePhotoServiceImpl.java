package com.ds.app.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ds.app.dto.response.ProfilePhotoResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.exception.EmployeeNotFoundException1;
import com.ds.app.exception.FileStorageException;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.AppUserRepository;
import com.ds.app.service.EmployeePhotoService;

@Service
public class EmployeePhotoServiceImpl implements EmployeePhotoService {
	
	@Autowired
	EmployeeRepository iEmployeeRepo;
	
	@Autowired
	AppUserRepository appUserRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	private static final Logger logger = LoggerFactory.getLogger(EmployeePhotoServiceImpl.class);

	


	@Override
	public ProfilePhotoResponseDTO uploadProfilePhoto(MultipartFile file, String username) throws Exception {
		
		        logger.info("Photo upload for username: {}", username);
		        Employee employee = iEmployeeRepo.findByUsername(username)
		                .orElseThrow(() -> new EmployeeNotFoundException1("Employee not found: " + username));
		        validateFile(file);
		        deleteOldPhoto(employee.getProfilePhotoUrl());
		        String newPath = saveNewPhoto(file, employee.getUserId());
		        employee.setProfilePhotoUrl(newPath);
		        iEmployeeRepo.save(employee);
		        logger.info("Photo saved for userId: {}", employee.getUserId());
		        return new ProfilePhotoResponseDTO(newPath, "Uploaded successfully");
		 
	}
	
	 private void validateFile(MultipartFile file) throws Exception {
	        if (file == null || file.isEmpty()) throw new FileStorageException("File is required");
	        String ct = file.getContentType();
	        if (ct == null || !(ct.equals("image/jpeg") || ct.equals("image/png") || ct.equals("image/jpg")))
	            throw new FileStorageException("Only JPG, JPEG, PNG files allowed");
	        if (file.getSize() > 2 * 1024 * 1024) throw new FileStorageException("File size must be under 2MB");
	    }
	 
	    private void deleteOldPhoto(String oldPath) throws Exception {
	        if (oldPath == null) return;
	        try { Files.deleteIfExists(Paths.get(oldPath)); }
	        catch (IOException e) { throw new FileStorageException("Failed to delete old photo", e); }
	    }
	 
	    private String saveNewPhoto(MultipartFile file, Long userId) throws Exception {
	        try {
	            Path uploadDir = Paths.get("uploads/profile-photos");
	            if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
	            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
	            String fileName = userId + "_" + System.currentTimeMillis() + ext;
	            Path filePath = uploadDir.resolve(fileName);
	            Files.copy(file.getInputStream(), filePath);
	            return filePath.toString();
	        } catch (IOException e) { throw new FileStorageException("Failed to save photo", e); }
	    }

}//end class
