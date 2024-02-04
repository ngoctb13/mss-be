package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.StaffCreateDTO;
import vn.edu.fpt.be.dto.StaffDTO;
import vn.edu.fpt.be.service.StaffService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/staff")
public class StaffController {
    private final StaffService staffService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> createStaff(@RequestBody StaffCreateDTO staffCreateDTO) {
        try {
            staffService.createStaff(staffCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Staff member created successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the staff member.");
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<List<StaffDTO>> getAllStaffs() {
        try {
            List<StaffDTO> staffs = staffService.getAllStaffs();
            return ResponseEntity.ok(staffs);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-store/{storeId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<List<StaffDTO>> getStaffsByStore(@PathVariable Long storeId) {
        try {
            List<StaffDTO> staffs = staffService.getStaffsByStore(storeId);
            if (staffs.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(staffs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
