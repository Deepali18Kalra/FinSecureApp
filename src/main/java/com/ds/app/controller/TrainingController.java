package com.ds.app.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.ds.app.dto.request.EnrollRequestDTO;
import com.ds.app.dto.request.TrainingRequestDTO;
import com.ds.app.dto.response.EligibleEmployeeResponseDTO;
import com.ds.app.dto.response.EmployeeTrainingResponseDTO;
import com.ds.app.dto.response.TrainingResponseDTO;
import com.ds.app.service.TrainingService;

@RestController
@RequestMapping("/finsecure/hr/training")
public class TrainingController {

    @Autowired
    private TrainingService trainingService;
    
   

    //  CREATE TRAINING
    @PostMapping("/create")
    public TrainingResponseDTO createTraining(@RequestBody TrainingRequestDTO request) {
        return trainingService.createTraining(request);
    }

    //  ENROLL EMPLOYEES
    @PostMapping("/enroll")
    public String enrollEmployee(@RequestBody EnrollRequestDTO request) {
        return trainingService.enrollEmployee(request);
    }

    // START TRAINING
    @PutMapping("/{id}/start")
    public String startTraining(@PathVariable Long id) {
        return trainingService.startTraining(id);
    }

    // STOP TRAINING
    @PutMapping("/{id}/stop")
    public String stopTraining(@PathVariable Long id) {
        return trainingService.stopTraining(id);
    }

    //  GET ALL TRAININGS
    @GetMapping
    public Page<TrainingResponseDTO> getAllTrainings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return trainingService.getAllTrainings(page, size);
    }

    //  GET TRAINING BY ID
    @GetMapping("/{id}")
    public TrainingResponseDTO getTrainingById(@PathVariable Long id) {
        return trainingService.getTrainingById(id);
    }

    //  GET MY TRAINING
    @GetMapping("/my")
    public Page<EmployeeTrainingResponseDTO> getMyTraining(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return trainingService.getMyTraining(page, size);
    }

    //  CHECK IF TRAINING COMPLETED
    @GetMapping("/completed/{employeeId}")
    public Boolean isTrainingCompleted(@PathVariable Long employeeId) {
        return trainingService.isTrainingCompleted(employeeId);
    }

    //  SOFT DELETE TRAINING
    @DeleteMapping("/{id}")
    public String deleteTraining(@PathVariable Long id) {
        return trainingService.deleteTraining(id);
    }

}