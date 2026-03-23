package com.ds.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finsecure/finance")
public class FinanceController {

	@GetMapping("/test")
	public ResponseEntity<String> test(){
		return new ResponseEntity<String>("successfuly accessing finance controller",HttpStatus.OK);
	}
}
