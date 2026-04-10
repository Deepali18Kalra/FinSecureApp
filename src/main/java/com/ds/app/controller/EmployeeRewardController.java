package com.ds.app.controller;
 
import java.util.List;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
import com.ds.app.dto.response.EmployeeRewardResponseDTO;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.EmployeeRewardService;

 
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
 
@RestController
@Tag(name = "Recognition & Rewards - Employee", description = "Employee views own rewards and recognitions")
public class EmployeeRewardController {
 
    private static final Logger logger = LoggerFactory.getLogger(EmployeeRewardController.class);
 
    @Autowired
    private EmployeeRewardService rewardService;
 
    @Autowired
    private EmployeeRepository employeeRepository;
 
    // ── Helper — get userId from JWT username ──────────────────────
    private Long getUserId(String username) {
        return employeeRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + username))
                .getUserId();
    }
 
   
    @GetMapping("/finsecure/employee/rewards")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @Operation(
        summary = "Get my rewards (Employee)",
        description =
            "Employee views all own rewards and recognitions.\n\n" +
            "Sorted by reward date — most recent first.\n\n" +
            "No userId needed — resolved from JWT token automatically."
    )
    public ResponseEntity<List<EmployeeRewardResponseDTO>> getMyRewards(@AuthenticationPrincipal UserDetails userDetails) {
 
        logger.info("Fetching rewards for: {}",userDetails.getUsername());
 
        Long userId = getUserId(userDetails.getUsername());
 
        return ResponseEntity.ok(rewardService.getMyRewards(userId));
    }
}