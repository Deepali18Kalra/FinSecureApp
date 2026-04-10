package com.ds.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.app.dto.response.EmployeeProfileResponseDTO;
import com.ds.app.dto.response.PagedResponseDTO;
import com.ds.app.exception.EmployeeNotFoundException1;
import com.ds.app.service.EmployeeAdminService;
import com.ds.app.service.impl.EmployeePhotoServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/finsecure/admin")
public class AdminController {
	
	

	@GetMapping("/test")
	public ResponseEntity<String> test(){
		return new ResponseEntity<String>("successfuly accessing admin controller",HttpStatus.OK);
	}
	

}//end class
