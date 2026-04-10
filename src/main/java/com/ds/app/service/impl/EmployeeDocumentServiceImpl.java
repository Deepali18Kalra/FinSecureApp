package com.ds.app.service.impl;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ds.app.dto.request.EmployeeDocumentRequestDTO;
import com.ds.app.dto.response.EmployeeDocumentResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeDocument;
import com.ds.app.exception.FileStorageException;
import com.ds.app.repository.EmployeeDocumentRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.EmployeeDocumentService;
import com.ds.app.utils.MaskingUtil;

@Service
@Transactional
public class EmployeeDocumentServiceImpl implements EmployeeDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeDocumentServiceImpl.class);

    private static final long MAX_SIZE = 5 * 1024 * 1024;
    
    private static final List<String> ALLOWED = List.of("image/jpeg", "image/png", "image/jpg", "application/pdf");

   @Autowired
   EmployeeDocumentRepository documentRepo;
   
   @Autowired
   EmployeeRepository iEmployeeRepository;


       @Override
       public EmployeeDocumentResponseDTO uploadDocument( EmployeeDocumentRequestDTO dto, MultipartFile file,Long userId) throws Exception {

           logger.info("Uploading {} for userId: {}", dto.getDocumentType(), userId);

           Employee employee = iEmployeeRepository.findByUserId(userId)
                   .orElseThrow(() -> new RuntimeException("Employee not found: " + userId));
           validateFile(file);
           if (documentRepo.existsByEmployeeUserIdAndDocumentType( userId, dto.getDocumentType())) {
               throw new RuntimeException(dto.getDocumentType() + " already uploaded. Delete existing one first.");
           }

           String filePath = saveFile( file, userId, dto.getDocumentType());

           EmployeeDocument document = new EmployeeDocument();

           document.setEmployee(employee);  
           document.setDocumentType(dto.getDocumentType());
           document.setDocumentNumber(dto.getDocumentNumber());
           document.setDocumentUrl(filePath);
           document.setExpiryDate(dto.getExpiryDate());
           document.setIsVerified(false);

           EmployeeDocument saved = documentRepo.save(document);
           logger.info("Document saved for userId: {}", userId);
           return mapToResponse(saved, false);
       }

       @Override
       @Transactional(readOnly = true)
       public List<EmployeeDocumentResponseDTO> getMyDocuments(Long userId) {
           logger.info("Fetching documents for userId: {}", userId);
           return documentRepo.findByEmployeeUserId(userId)
                   .stream()
                   .map(d -> mapToResponse(d, false))
                   .toList();
       }

       @Override
       public void deleteDocument(Integer documentId, Long userId) {
    	   
           logger.warn("Deleting documentId: {} for userId: {}", documentId, userId);

           EmployeeDocument document = documentRepo
                   .findByDocumentIdAndEmployeeUserId(documentId, userId)
                   .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));

           deleteFile(document.getDocumentUrl());
           documentRepo.delete(document);
           logger.info("Document deleted: {}", documentId);
       }

       @Override
       @Transactional(readOnly = true)
       public List<EmployeeDocumentResponseDTO> getDocumentsByUserId(Long userId) {
           logger.info("HR fetching documents for userId: {}", userId);
           return documentRepo.findByEmployeeUserId(userId)
                   .stream()
                   .map(d -> mapToResponse(d, true))
                   .toList();
       }

       @Override
       public EmployeeDocumentResponseDTO verifyDocument(Integer documentId) {
           logger.info("Verifying documentId: {}", documentId);

           EmployeeDocument document = documentRepo
                   .findById(documentId)
                   .orElseThrow(() -> new RuntimeException( "Document not found: " + documentId));

           document.setIsVerified(true);

           EmployeeDocument saved = documentRepo.save(document);
           return mapToResponse(saved, false);
       }

       private EmployeeDocumentResponseDTO mapToResponse( EmployeeDocument d, boolean masked) {
           return EmployeeDocumentResponseDTO.builder()
                   .documentId(d.getDocumentId()) 
                   .userId(d.getEmployee().getUserId())         
                   .documentType(d.getDocumentType())
                   .documentNumber(
                           masked && d.getDocumentNumber() != null ? MaskingUtil.maskLast(4, d.getDocumentNumber())
                           : d.getDocumentNumber())
                   .documentUrl(d.getDocumentUrl())
                   .expiryDate(d.getExpiryDate())
                   .isVerified(d.getIsVerified())
                   .createdAt(d.getCreatedAt())
                   .updatedAt(d.getUpdatedAt())
                   .build();
       }

       private void validateFile(MultipartFile file) throws Exception {
           if (file == null || file.isEmpty())
               throw new FileStorageException("File is required");
           if (!ALLOWED.contains(file.getContentType()))
               throw new FileStorageException("Only PDF, JPG, JPEG, PNG allowed");
           if (file.getSize() > MAX_SIZE)
               throw new FileStorageException( "File must be under 5MB");
       }

       private String saveFile(MultipartFile file,Long userId,String docType) throws Exception {
           try {
               Path uploadDir = Paths.get( "uploads/documents/" + userId);
               if (!Files.exists(uploadDir))
                   Files.createDirectories(uploadDir);
               String originalName = file.getOriginalFilename();
               String ext = originalName.substring(originalName.lastIndexOf("."));
               String fileName = docType + "_" +System.currentTimeMillis() + ext;
               Path filePath = uploadDir.resolve(fileName);
               Files.copy(file.getInputStream(), filePath);
               return filePath.toString();
           } catch (IOException e) {
               throw new FileStorageException( "Failed to save document", e);
           }
       }

       private void deleteFile(String filePath) {
           if (filePath == null) return;
           try {
               Files.deleteIfExists(Paths.get(filePath));
           } catch (IOException e) {
               logger.error("Failed to delete: {}", filePath);
           }
       }
 
}//end class
