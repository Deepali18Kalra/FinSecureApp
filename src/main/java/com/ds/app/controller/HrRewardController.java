package com.ds.app.controller;
 
import java.util.List;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
import com.ds.app.dto.request.EmployeeRewardRequestDTO;
import com.ds.app.dto.response.EmployeeRewardResponseDTO;
import com.ds.app.service.EmployeeRewardService;

 
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
 
@RestController
@RequestMapping("/finsecure/hr")
@Tag(name = "Recognition & Rewards - HR",description = "HR manages employee rewards and recognitions")
public class HrRewardController {
 
    private static final Logger logger =
            LoggerFactory.getLogger(HrRewardController.class);
 
    @Autowired
    private EmployeeRewardService rewardService;
 
   
    @PostMapping("/finsecure/hr/employee/{userId}/rewards")
    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
    @Operation(
        summary = "Give reward to employee (HR / Admin)",
        description =
            "HR gives recognition or reward to an employee.\n\n" +
            "**RewardType:** EMPLOYEE_OF_MONTH, BEST_PERFORMER, " +
            "INNOVATION_AWARD, TEAM_PLAYER, LEADERSHIP_AWARD...\n\n" +
            "**RewardCategory:** PERFORMANCE, INNOVATION, " +
            "TEAMWORK, LEADERSHIP, CLIENT_SATISFACTION, SPECIAL\n\n" +
            "Find userId via GET /finsecure/hr/employees/search first."
    )
    public ResponseEntity<EmployeeRewardResponseDTO> giveReward(@PathVariable Long userId,
            @Valid @RequestBody EmployeeRewardRequestDTO dto, @AuthenticationPrincipal UserDetails userDetails) throws Exception {
 
        logger.info("HR '{}' giving reward to userId: {}",userDetails.getUsername(), userId);
 
        return new ResponseEntity<>(rewardService.giveReward(userId,dto,userDetails.getUsername()),HttpStatus.CREATED);
    }
 
   
    @GetMapping("/finsecure/hr/employee/{userId}/rewards")
    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
    @Operation(
        summary = "Get employee rewards (HR / Admin)",
        description =
            "HR views all rewards given to a specific employee.\n\n" +
            "Sorted by reward date — most recent first.\n\n" +
            "Find userId via GET /finsecure/hr/employees/search first."
    )
    public ResponseEntity<List<EmployeeRewardResponseDTO>> getRewardsByUserId(@PathVariable Long userId) {
 
        logger.info("HR fetching rewards for userId: {}",userId);
 
        return ResponseEntity.ok(rewardService.getRewardsByUserId(userId));
    }
 
 
    @PutMapping("/finsecure/hr/rewards/{rewardId}")
    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
    @Operation(
        summary = "Update reward (HR / Admin)",
        description =
            "HR updates an existing reward record.\n\n" +
            "Partial update — only send fields you want to change.\n\n" +
            "All fields are optional."
    )
    public ResponseEntity<EmployeeRewardResponseDTO> updateReward(@PathVariable Integer rewardId,@Valid @RequestBody EmployeeRewardRequestDTO dto) {
 
        logger.info("Updating rewardId: {}", rewardId);
 
        return ResponseEntity.ok(rewardService.updateReward(rewardId, dto));
    }
 
   
    @DeleteMapping("/finsecure/hr/rewards/{rewardId}")
    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
    @Operation(
        summary = "Delete reward (HR / Admin)",
        description =
            "Permanently deletes a reward record.\n\n" +
            "This action cannot be undone."
    )
    public ResponseEntity<String> deleteReward(@PathVariable Integer rewardId) {
 
        logger.warn("Deleting rewardId: {}", rewardId);
 
        rewardService.deleteReward(rewardId);
 
        return ResponseEntity.ok("Reward deleted successfully: " + rewardId);
    }
 
    @GetMapping("/employees/top-rewarded")
    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
    @Operation(
        summary = "Top rewarded employees (HR / Admin)",
        description =
            "Returns employees ranked by number of rewards received.\n\n" +
            "Response format: [[userId, rewardCount], ...]\n\n" +
            "HR uses this for performance overview and recognition."
    )
    public ResponseEntity<List<Object[]>> getTopRewarded() {
 
        logger.info("Fetching top rewarded employees");
 
        return ResponseEntity.ok(rewardService.getTopRewardedEmployees());
    }
}