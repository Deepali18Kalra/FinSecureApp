package com.ds.app.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class SalaryJob {
	
	 	@Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private Long id;
}
