package com.ds.app.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ds.app.dto.request.EnrollRequestDTO;
import com.ds.app.dto.request.TrainingRequestDTO;
import com.ds.app.dto.response.*;
import com.ds.app.exception.ApiResponse;
import com.ds.app.service.IEmailService;
import com.ds.app.service.TrainingService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/finsecure/training")
public class TrainingController {

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private IEmailService emailService;

    // TEST EMAIL ENDPOINT
    @GetMapping("/test-email")
    public String testEmail() {
        log.info("START: testEmail endpoint called");
        emailService.sendEnrollmentEmail(
                "saurabhkumar.mca24@bvicam.in",
                "Saurabh",
                "spring training"
        );
        log.info("END: testEmail | Email sent successfully");
        return "Email sent";
    }

    // CREATE TRAINING
    @PreAuthorize("hasAuthority('HR')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TrainingResponseDTO>> createTraining(
            @Valid @RequestBody TrainingRequestDTO request) {

        log.info("START: createTraining | TrainingName: {}", request.getTrainingName());
        TrainingResponseDTO data = trainingService.createTraining(request);
        log.info("END: createTraining | TrainingId: {}", data.getTrainingId());

        return new ResponseEntity<>(
                new ApiResponse<>(LocalDateTime.now(), HttpStatus.CREATED.value(),
                        "Training created successfully", data),
                HttpStatus.CREATED
        );
    }
    

    // GET ELIGIBLE EMPLOYEES
    @PreAuthorize("hasAuthority('HR')")
    @GetMapping("/eligible")
    public ResponseEntity<ApiResponse<Page<EligibleEmployeeResponseDTO>>> getEligibleEmployee(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("START: getEligibleEmployee | DepartmentId: {}", departmentId);
        Page<EligibleEmployeeResponseDTO> data =
                trainingService.getEligibleEmployees(departmentId, page, size);
        log.info("END: getEligibleEmployee | Count: {}", data.getTotalElements());

        return ResponseEntity.ok(
                new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                        "Eligible employees fetched", data)
        );
    }

    // ENROLL EMPLOYEE
    @PreAuthorize("hasAuthority('HR')")
    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<String>> enrollEmployee(
            @Valid @RequestBody EnrollRequestDTO request) {

        log.info("START: enrollEmployee | TrainingId: {}", request.getTrainingId());
        String msg = trainingService.enrollEmployee(request);
        log.info("END: enrollEmployee | Result: {}", msg);

        return ResponseEntity.ok(
                new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                        msg, null)
        );
    }

    // START TRAINING
    @PreAuthorize("hasAuthority('HR')")
    @PutMapping("/{id}/start")
    public ResponseEntity<ApiResponse<String>> startTraining(@PathVariable Long id) {
        log.info("START: startTraining | TrainingId: {}", id);
        String msg = trainingService.startTraining(id);
        log.info("END: startTraining | Result: {}", msg);

        return ResponseEntity.ok(
                new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                        msg, null)
        );
    }

    // STOP TRAINING
    @PreAuthorize("hasAuthority('HR')")
    @PutMapping("/{id}/stop")
    public ResponseEntity<ApiResponse<String>> stopTraining(@PathVariable Long id) {
        log.info("START: stopTraining | TrainingId: {}", id);
        String msg = trainingService.stopTraining(id);
        log.info("END: stopTraining | Result: {}", msg);

        return ResponseEntity.ok(
                new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                        msg, null)
        );
    }

    // GET ALL TRAININGS
    @PreAuthorize("hasAuthority('HR')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<TrainingResponseDTO>>> getAllTrainings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("START: getAllTrainings | Page: {} Size: {}", page, size);
        Page<TrainingResponseDTO> data = trainingService.getAllTrainings(page, size);
        log.info("END: getAllTrainings | Count: {}", data.getTotalElements());

        return ResponseEntity.ok(
                new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                        "Trainings fetched", data)
        );
    }

    // GET TRAINING BY ID
    @PreAuthorize("hasAnyAuthority('HR','EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TrainingResponseDTO>> getTrainingById(
            @PathVariable Long id) {

        log.info("START: getTrainingById | TrainingId: {}", id);
        TrainingResponseDTO data = trainingService.getTrainingById(id);
        log.info("END: getTrainingById | TrainingName: {}", data.getTrainingName());

        return ResponseEntity.ok(
                new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                        "Training fetched", data)
        );
    }

    // GET MY TRAININGS
    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<EmployeeTrainingResponseDTO>>> getMyTraining(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("START: getMyTraining | Page: {} Size: {}", page, size);
        Page<EmployeeTrainingResponseDTO> data = trainingService.getMyTraining(page, size);
        log.info("END: getMyTraining | Count: {}", data.getTotalElements());

        return ResponseEntity.ok(
                new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                        "My trainings fetched", data)
        );
    }

    // CHECK COMPLETION
    @PreAuthorize("hasAnyAuthority('HR','EMPLOYEE')")
    @GetMapping("/completed/{employeeId}/{trainingId}")
    public ResponseEntity<ApiResponse<Boolean>> isTrainingCompleted(
            @PathVariable Long employeeId,
            @PathVariable Long trainingId) {

        log.info("START: isTrainingCompleted | EmployeeId: {} TrainingId: {}", employeeId, trainingId);
        Boolean result = trainingService.isTrainingCompleted(employeeId, trainingId);
        log.info("END: isTrainingCompleted | Result: {}", result);

        return ResponseEntity.ok(
                new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                        "Training completion status", result)
        );
    }

    // DELETE TRAINING
    @PreAuthorize("hasAuthority('HR')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse<String>> deleteTraining(@PathVariable Long id) {
        log.info("START: deleteTraining | TrainingId: {}", id);
        String msg = trainingService.deleteTraining(id);
        log.info("END: deleteTraining | Result: {}", msg);

        return ResponseEntity.ok(
                new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                        msg, null)
        );
    }
}
