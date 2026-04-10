package com.ds.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.ds.app.dto.response.ProfilePhotoResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.enums.UserRole;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.impl.EmployeePhotoServiceImpl;

@ExtendWith(MockitoExtension.class)
class EmployeePhotoServiceImplTest {

    @Mock
    private EmployeeRepository iEmployeeRepo;

    @InjectMocks
    private EmployeePhotoServiceImpl photoService;

    private Employee mockEmployee;

    @BeforeEach
    void setUp() {
        mockEmployee = new Employee();
        mockEmployee.setUserId(13L);
        mockEmployee.setUsername("john_doe");
        mockEmployee.setRole(UserRole.EMPLOYEE);
        mockEmployee.setIsDeleted(false);
        mockEmployee.setIsAccountLocked(false);
        mockEmployee.setIsEscalated(false);
        mockEmployee.setProfilePhotoUrl(null);
    }

  
    //  uploadProfilePhoto — success
    @Test
    @Description("upload profile photo method test")
    void testUploadProfilePhoto() throws Exception {

        // Sample Input for actual method call
        // MockMultipartFile simulates file upload
        MockMultipartFile file = new MockMultipartFile(
                "file",                    // field name
                "photo.jpg",               // original filename
                "image/jpeg",              // content type
                "fake image content".getBytes() // content
        );
        String username = "john_doe";

        // Expected Output from Repo
        Employee savedEmployee = new Employee();
        savedEmployee.setUserId(13L);
        savedEmployee.setUsername("john_doe");
        savedEmployee.setProfilePhotoUrl("uploads/photos/john_doe_photo.jpg");

        // Code to configure through mockito
        when(iEmployeeRepo.findByUsername("john_doe")).thenReturn(Optional.of(mockEmployee));
        when(iEmployeeRepo.save(any(Employee.class))).thenReturn(savedEmployee);

        // Call actual method
        ProfilePhotoResponseDTO result = photoService.uploadProfilePhoto(file, username);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getProfilePhotoUrl());
        verify(iEmployeeRepo, times(1)).save(any(Employee.class));
    }

   
    //  uploadProfilePhoto — employee not found
    @Test
    @Description("upload photo throws exception when employee not found test")
    void testUploadProfilePhoto_NotFound() {

        // Sample Input
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );
        String username = "unknown_user";

        // Code to configure through mockito
        when(iEmployeeRepo.findByUsername("unknown_user")).thenReturn(Optional.empty());

        // Call actual method and Assert exception
        assertThrows(Exception.class,
                () -> photoService.uploadProfilePhoto(file, username));

        // Verify save was never called
        verify(iEmployeeRepo, never()).save(any(Employee.class));
    }

   
    //  uploadProfilePhoto — wrong file type
    @Test
    @Description("upload photo throws exception for wrong file type test")
    void testUploadProfilePhoto_WrongFileType() {

        // Sample Input — PDF not allowed for photo
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",  // ← wrong type for photo
                "fake pdf content".getBytes()
        );
        String username = "john_doe";

        // Code to configure through mockito
        when(iEmployeeRepo.findByUsername("john_doe")).thenReturn(Optional.of(mockEmployee));

        // Call actual method and Assert exception
        assertThrows(Exception.class,
                () -> photoService.uploadProfilePhoto(file, username));
    }
    
}//end class