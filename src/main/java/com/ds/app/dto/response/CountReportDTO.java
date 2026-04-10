package com.ds.app.dto.response;

import com.ds.app.enums.EmployeeExperience;
import com.ds.app.enums.EmploymentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountReportDTO {
	
	
	    // ── Employee status ────────────────────────────
	    private Long totalDeleted;

	    // ── Profile completion ─────────────────────────
	    private Long incompleteProfiles;
	    private Long withoutPhoto;

	    // ── Education ──────────────────────────────────
	    private Long withEducation;
	    private Long withoutEducation;

	    // ── Documents ──────────────────────────────────
	    private Long withDocuments;
	    private Long withoutDocuments;

	    // ── Rewards ────────────────────────────────────
	    private Long withRewards;

}
