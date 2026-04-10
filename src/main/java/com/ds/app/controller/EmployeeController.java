package com.ds.app.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ds.app.dto.request.*;
import com.ds.app.dto.response.*;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.EmployeeCRUDService;
import com.ds.app.service.EmployeePhotoService;
import com.ds.app.service.EmployeeRewardService;
import com.ds.app.service.impl.EmployeePhotoServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/finsecure/employee")
public class EmployeeController {
   
	@GetMapping("/test")
	public ResponseEntity<String> test() {
		
		return new ResponseEntity<String>("successfuly accessing employee controller",HttpStatus.OK);
	}
}//end class

