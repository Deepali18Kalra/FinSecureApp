package com.ds.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ds.app.entity.TimesheetEntry;

public interface ITimesheetEntryRepository extends JpaRepository<TimesheetEntry, Long>{

}
