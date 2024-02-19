package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.BagTypeDTO;
import vn.edu.fpt.be.dto.BagTypeRequest;
import vn.edu.fpt.be.service.BagTypeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bagtypes")
public class BagTypeController {
    private final BagTypeService bagTypeService;
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> createBagType(@RequestBody BagTypeRequest bagTypeRequest) {
        try {
            BagTypeDTO createdBagType = bagTypeService.createBagType(bagTypeRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBagType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the bag type.");
        }
    }
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> getAllBagTypes() {
        try {
            List<BagTypeDTO> bagTypes = bagTypeService.getAllBagTypes();
            return ResponseEntity.status(HttpStatus.OK).body(bagTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching all bag types.");
        }
    }
    @PutMapping("/update/{bagTypeId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> updateBagType(@RequestBody BagTypeRequest bagTypeRequest,
                                           @PathVariable Long bagTypeId) {
        try {
            BagTypeDTO updatedBagType = bagTypeService.updateBagType(bagTypeRequest, bagTypeId);
            return ResponseEntity.status(HttpStatus.OK).body(updatedBagType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the bag type.");
        }
    }
    @PutMapping("/deactivate/{bagTypeId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> deactivateBagType(@PathVariable Long bagTypeId) {
        try {
            BagTypeDTO deactivatedBagType = bagTypeService.deactivate(bagTypeId);
            return ResponseEntity.status(HttpStatus.OK).body(deactivatedBagType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deactivating the bag type.");
        }
    }
}
