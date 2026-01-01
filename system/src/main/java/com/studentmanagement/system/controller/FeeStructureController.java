package com.studentmanagement.system.controller;

import com.studentmanagement.system.dto.FeeStructureRequest;
import com.studentmanagement.system.model.FeeStructure;
import com.studentmanagement.system.service.FeeStructureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fee-structure")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class FeeStructureController {

    private final FeeStructureService feeStructureService;

    /**
     * Create or update fee structure (ADMIN only)
     * POST /api/fee-structure
     */
    @PostMapping
    public ResponseEntity<FeeStructure> createOrUpdateFeeStructure(
            @Valid @RequestBody FeeStructureRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        validateAdminAccess(role);

        FeeStructure feeStructure = feeStructureService.createOrUpdateFeeStructure(request, userId);
        return new ResponseEntity<>(feeStructure, HttpStatus.CREATED);
    }

    /**
     * Get fee structure by class name
     * GET /api/fee-structure/{className}
     */
    @GetMapping("/{className}")
    public ResponseEntity<FeeStructure> getFeeStructureByClass(
            @PathVariable String className,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        FeeStructure feeStructure = feeStructureService.getFeeStructureByClass(className);
        return ResponseEntity.ok(feeStructure);
    }

    /**
     * Get all fee structures (ADMIN only)
     * GET /api/fee-structure
     */
    @GetMapping
    public ResponseEntity<List<FeeStructure>> getAllFeeStructures(
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        List<FeeStructure> feeStructures = feeStructureService.getAllFeeStructures();
        return ResponseEntity.ok(feeStructures);
    }

    /**
     * Delete fee structure (ADMIN only)
     * DELETE /api/fee-structure/{className}
     */
    @DeleteMapping("/{className}")
    public ResponseEntity<?> deleteFeeStructure(
            @PathVariable String className,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        feeStructureService.deleteFeeStructure(className);
        return ResponseEntity.ok(
                Map.of("message", "Fee structure deleted for class: " + className)
        );
    }

    private void validateAdminAccess(String role) {
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Admin access required"
            );
        }
    }
}