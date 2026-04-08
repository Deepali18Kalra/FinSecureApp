package com.ds.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ds.app.dto.AssetEscalationDTO;
import com.ds.app.dto.RaiseEscalationRequestDTO;
import com.ds.app.entity.AppUser;
import com.ds.app.entity.Asset;
import com.ds.app.entity.AssetEscalation;
import com.ds.app.entity.Employee;
import com.ds.app.enums.EscalationStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.AssetEscalationRepository;
import com.ds.app.repository.AssetRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.service.AssetEscalationService;
import com.ds.app.exception.BadRequestException;

@Service
public class AssetEscalationServiceImpl implements AssetEscalationService {
	
	private final AssetRepository assetRepository;
	private final AssetEscalationRepository assetEscalationRepository;
	private final iAppUserRepository appUserRepository;
	private final EmployeeRepository employeeRepository;
	
	public AssetEscalationServiceImpl(AssetRepository assetRepository,
			AssetEscalationRepository assetEscalationRepository, iAppUserRepository appUserRepository,EmployeeRepository employeeRepository) {
		super();
		this.assetRepository = assetRepository;
		this.assetEscalationRepository = assetEscalationRepository;
		this.appUserRepository = appUserRepository;
		this.employeeRepository = employeeRepository;
	}
	
	

@Override
    public AssetEscalationDTO raiseEscalation(RaiseEscalationRequestDTO request,Long hrOrAdminUserId) {


        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

        AppUser user = appUserRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee user not found"));

        if (!(user instanceof Employee employee)) {
            throw new BadRequestException("Selected user is not an Employee");
        }

        AppUser raisedByUser = appUserRepository.findById(hrOrAdminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Raiser user not found"));

        AssetEscalation esc = new AssetEscalation();
        esc.setAsset(asset);
        esc.setEmployee(employee);         //Employeemisused the asset
        esc.setRaisedBy(raisedByUser);         //HR/Admin raising escalation
        esc.setReason(request.getReason());
        esc.setStatus(EscalationStatus.OPEN);
        esc.setCreatedAt(LocalDateTime.now());
        

        
        assetEscalationRepository.save(esc);


employee.setHasActiveAssetEscalation(true);
employeeRepository.save(employee);

        AssetEscalationDTO dto = new AssetEscalationDTO();
        dto.setEscalationId(esc.getEscalationId());
        dto.setAssetId(asset.getAssetId());
        dto.setEmployeeId(employee.getUserId());
        dto.setRaisedBy(raisedByUser.getUserId());
        dto.setReason(esc.getReason());
        dto.setStatus(esc.getStatus());
        dto.setCreatedAt(esc.getCreatedAt());

        return dto;
    }

	

@Override
   public AssetEscalationDTO updateEscalationStatus(Long escalationId,String status,Long hrOrAdminUserId) {

       AssetEscalation esc = assetEscalationRepository.findById(escalationId)
               .orElseThrow(() -> new ResourceNotFoundException("Escalation not found"));

       esc.setStatus(EscalationStatus.valueOf(status));
       esc.setUpdatedAt(LocalDateTime.now());

       assetEscalationRepository.save(esc);

       
       assetEscalationRepository.save(esc);

    Employee employee = esc.getEmployee();

    boolean hasActiveEscalations =
            assetEscalationRepository.existsByEmployeeAndStatus(
                    employee, EscalationStatus.OPEN
            );

    employee.setHasActiveAssetEscalation(hasActiveEscalations);
    employeeRepository.save(employee);
       
       AssetEscalationDTO dto = new AssetEscalationDTO();
       dto.setEscalationId(esc.getEscalationId());
       dto.setStatus(esc.getStatus());

       return dto;
}


@Override
public List<AssetEscalationDTO> getAllEscalations() {

    return assetEscalationRepository.findAll()
            .stream()
            .map(escalation -> {
                AssetEscalationDTO dto = new AssetEscalationDTO();
                dto.setEscalationId(escalation.getEscalationId());
                dto.setAssetId(escalation.getAsset().getAssetId());
                dto.setEmployeeId(escalation.getEmployee().getUserId());
                dto.setRaisedBy(escalation.getRaisedBy().getUserId());
                dto.setReason(escalation.getReason());
                dto.setStatus(escalation.getStatus());
                dto.setCreatedAt(escalation.getCreatedAt());
                return dto;
            })
            .toList(); 
}




	@Override
	public Page<AssetEscalationDTO> getAllEscalationsPaginated(Pageable pageable) {

		return assetEscalationRepository.findAll(pageable)
            .map(escalation -> {
                AssetEscalationDTO dto = new AssetEscalationDTO();
                dto.setEscalationId(escalation.getEscalationId());
                dto.setAssetId(escalation.getAsset().getAssetId());
                dto.setEmployeeId(escalation.getEmployee().getUserId());
                dto.setRaisedBy(escalation.getRaisedBy().getUserId());
                dto.setReason(escalation.getReason());
                dto.setStatus(escalation.getStatus());
                dto.setCreatedAt(escalation.getCreatedAt());
                return dto;
            });
	}

}
