package com.ds.app.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Email;

import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.EmployeeExperience;
import com.ds.app.enums.EmploymentType;
import com.ds.app.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Employee extends AppUser {
	
	
	
	private String employeeCode;

	private String firstName;
	

	private String lastName;
	
	@Email(message = "Invalid email format")
	
	private String email;
	
	@Column(nullable = true, unique = true, length = 10)
    private String phoneNumber;
	

    @Column(nullable = true)
    private LocalDate dateOfBirth;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 20)
    private Gender gender;

    
    private String designation; 
    
   
    @PastOrPresent(message = "Joining date cannot be in the future")
    @Column(nullable = true)
    private LocalDate joiningDate;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType; 
        
   
    @Enumerated(EnumType.STRING)
    private CertificationStatus certificationStatus; 
    
    

	@Enumerated(EnumType.STRING)
    private EmployeeExperience employeeExperience; 
	
    @Column(length = 100)
    private String certificationName;
    
    @Column(nullable = true, length = 255)
    private String addressLine;

    @Column(nullable = true, length = 100)
    private String city;

    @Column(nullable = true, length = 100)
    private String state;

    @Column(nullable = true, length = 100)
    private String country;

    @Column(nullable = true, length = 6)
    private String pincode;
    
    
    @Column(nullable = false)
    private Boolean isDeleted = false;
    
  

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; 

    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private Boolean isEscalated = false; 

    @Size(max = 255, message = "Profile photo URL is too long")
    @Column(length = 255)
    private String profilePhotoUrl;
   
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    
 	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
 	private List<EmployeeDocument> documents;
 	
 	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
 	private List<EmployeeEducation> educations;
 	
 	@JsonIgnore
 	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch =FetchType.LAZY)
 	private List<EmployeeReward> rewards;

}// end class
