package com.studentmanagement.system.service;

import com.studentmanagement.system.dto.FeeStructureRequest;
import com.studentmanagement.system.model.FeeStructure;
import com.studentmanagement.system.repository.FeeStructureRepository;
import com.studentmanagement.system.util.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeStructureService {

    private final FeeStructureRepository feeStructureRepository;

    /**
     * Create or update fee structure for a class
     */
    public FeeStructure createOrUpdateFeeStructure(FeeStructureRequest request, String updatedBy) {
        AppLogger.info("Creating/Updating fee structure for class: " + request.getClassName());

        FeeStructure feeStructure = feeStructureRepository
                .findByClassName(request.getClassName())
                .orElse(new FeeStructure());

        feeStructure.setClassName(request.getClassName());
        feeStructure.setAdmissionFee(request.getAdmissionFee());
        feeStructure.setMonthlyFee(request.getMonthlyFee());
        feeStructure.setAnnualFee(request.getAnnualFee());
        feeStructure.setTransportFee(request.getTransportFee());
        feeStructure.setExamFee(request.getExamFee());
        feeStructure.setActivityFee(request.getActivityFee());
        feeStructure.setIsActive(true);
        feeStructure.setUpdatedBy(updatedBy);
        feeStructure.setUpdatedAt(LocalDateTime.now());

        if (feeStructure.getCreatedAt() == null) {
            feeStructure.setCreatedAt(LocalDateTime.now());
        }

        FeeStructure saved = feeStructureRepository.save(feeStructure);
        AppLogger.info("Fee structure saved for class: " + saved.getClassName());
        return saved;
    }

    /**
     * Get fee structure by class name
     */
    public FeeStructure getFeeStructureByClass(String className) {
        return feeStructureRepository.findByClassName(className)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fee structure not found for class: " + className
                ));
    }

    /**
     * Get all fee structures
     */
    public List<FeeStructure> getAllFeeStructures() {
        return feeStructureRepository.findAll();
    }

    /**
     * Delete fee structure
     */
    public boolean deleteFeeStructure(String className) {
        FeeStructure feeStructure = getFeeStructureByClass(className);
        feeStructureRepository.delete(feeStructure);
        AppLogger.info("Fee structure deleted for class: " + className);
        return true;
    }
}